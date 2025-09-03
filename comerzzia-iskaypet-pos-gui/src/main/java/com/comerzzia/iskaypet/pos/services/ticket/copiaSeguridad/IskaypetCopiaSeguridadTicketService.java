package com.comerzzia.iskaypet.pos.services.ticket.copiaSeguridad;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.comerzzia.iskaypet.pos.services.ticket.cupones.IskaypetCuponEmitidoTicket;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.iskaypet.pos.gui.ventas.tickets.IskaypetTicketManager;
import com.comerzzia.iskaypet.pos.services.ticket.IskaypetTicketVentaAbono;
import com.comerzzia.iskaypet.pos.services.ticket.cabecera.IskaypetCabeceraTicket;
import com.comerzzia.iskaypet.pos.services.ticket.cabecera.adicionales.DatosOrigenTicketBean;
import com.comerzzia.iskaypet.pos.services.ticket.lineas.IskaypetLineaTicket;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.persistence.tickets.aparcados.TicketAparcadoBean;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.core.documentos.Documentos;
import com.comerzzia.pos.services.promociones.PromocionesServiceException;
import com.comerzzia.pos.services.ticket.TicketVenta;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.services.ticket.cabecera.DatosDocumentoOrigenTicket;
import com.comerzzia.pos.services.ticket.copiaSeguridad.CopiaSeguridadTicketService;
import com.comerzzia.pos.services.ticket.profesional.TotalesTicketProfesional;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.xml.MarshallUtil;
import com.comerzzia.pos.util.xml.MarshallUtilException;

@Primary
@Component
@SuppressWarnings("rawtypes")
public class IskaypetCopiaSeguridadTicketService extends CopiaSeguridadTicketService{
	
	@Autowired
	protected IskaypetTicketManager ticketManager;
	
	@Override
	public synchronized TicketAparcadoBean prepararTicketAparcado(TicketVenta ticket)
			throws MarshallUtilException, UnsupportedEncodingException {
		TicketAparcadoBean ta = new TicketAparcadoBean();
		// Seteamos los valores para el nuevo bean
		// Obtenemos el xml del ticket
		List<Class<?>> clasesAux = new ArrayList<Class<?>>();
		clasesAux.add(SpringContext.getBean(IskaypetCabeceraTicket.class).getClass());
		clasesAux.add(SpringContext.getBean(IskaypetLineaTicket.class).getClass());
		clasesAux.add(SpringContext.getBean(IskaypetTicketVentaAbono.class).getClass());
		clasesAux.add(SpringContext.getBean(IskaypetCuponEmitidoTicket.class).getClass());
		clasesAux.add(TotalesTicketProfesional.class);
		
		recuperacionExtension(ticket);
		
		byte[] xmlTicket = MarshallUtil.crearXML(ticket, clasesAux);
		log.trace("TICKET: " + ticket.getUidTicket() + "\n" + new String(xmlTicket, "UTF-8") + "\n");

		ta.setTicket(xmlTicket);
		// Atributos del ticket
		ta.setCodAlmacen(ticket.getTienda().getCodAlmacen());
		ta.setFecha(new Date());
		ta.setUidTicket(ticket.getUidTicket());
		ta.setUidActividad(sesion.getAplicacion().getUidActividad());

		// Atributos particulares de ticket aparcado
		ta.setUsuario(ticket.getCajero().getUsuario());
		ta.setCodCaja(ticket.getCodCaja());
		if (ticket.getCliente() != null) {
			ta.setCodCliente(ticket.getCliente().getCodCliente());
		}
		ta.setNumArticulos(ticket.getLineas().size());
		ta.setImporte(ticket.getTotales().getTotal());
		ta.setIdTipoDocumento(ticket.getCabecera().getTipoDocumento());
		return ta;
	}

	private void recuperacionExtension(TicketVenta ticket) {
		//Se hace este proceso para evitar saltos de ticket a la hora de recuperar tickets con pagos anotados para los casos de ATCUD de Portugal
		String claveAtcud = null;
		
		if(!((TicketVentaAbono)ticket).getExtensiones().containsKey(IskaypetTicketManager.EXTENSION_RANGE_ID) ) {
			try {
				claveAtcud = ticketManager.comprobarGeneracionATCUD(ticket.getCabecera().getCodTipoDocumento());
			} catch (Exception e) {
				log.error("Error a la hora de recuperar el rango de ATCUD correspondiente: " + e.getMessage(), e);
			}
			
			if(StringUtils.isNotBlank(claveAtcud)) {
				((TicketVentaAbono)ticket).getExtensiones().put(IskaypetTicketManager.EXTENSION_RANGE_ID, claveAtcud);
			}	    				
		}
	}
	
	@Override
	public void clearBackupReturns() {
		try {
		
			TipoDocumentoBean tipoDocumentoActivo = sesion.getAplicacion().getDocumentos().getDocumento(Documentos.NOTA_CREDITO);
			TicketAparcadoBean copiaSeguridad = consultarCopiaSeguridadTicket(tipoDocumentoActivo);
			
			if(copiaSeguridad == null) {
				try {
					tipoDocumentoActivo = sesion.getAplicacion().getDocumentos().getDocumento("NS");
					copiaSeguridad = consultarCopiaSeguridadTicket(tipoDocumentoActivo);
					
				}catch (DocumentoException e) {
					log.error(e.getMessage());
				}
			}
			
			if(copiaSeguridad != null) {
				IskaypetTicketVentaAbono ticketRecuperado = (IskaypetTicketVentaAbono) MarshallUtil.leerXML(copiaSeguridad.getTicket(), ticketManager.getTicketClasses(tipoDocumentoActivo).toArray(new Class[] {}));

				if (ticketRecuperado != null) {
					if (ticketRecuperado.getIdTicket() != null ) {

						try {
							ticketManager.recuperarCopiaSeguridadTicket(null, copiaSeguridad);
							
							if(ticketRecuperado.getCabecera().getDatosDocOrigen() != null) {
								ticketManager.getTicket().getCabecera().setDatosDocOrigen(ticketRecuperado.getCabecera().getDatosDocOrigen());
							}
							else {
								DatosOrigenTicketBean datosOrigenDevolucionFalsos = ((IskaypetCabeceraTicket )ticketRecuperado.getCabecera()).getDatosOrigenFalsos();
								
								if (datosOrigenDevolucionFalsos != null) {
									DatosDocumentoOrigenTicket docOrigen = new DatosDocumentoOrigenTicket();
									docOrigen.setSerie(datosOrigenDevolucionFalsos.getSerie());
									docOrigen.setCaja(datosOrigenDevolucionFalsos.getCodCaja());
									docOrigen.setNumFactura(datosOrigenDevolucionFalsos.getNumFactura());
									docOrigen.setIdTipoDoc(datosOrigenDevolucionFalsos.getIdTipoDocumento());
									docOrigen.setCodTipoDoc(datosOrigenDevolucionFalsos.getCodTipoDocumento());
									docOrigen.setUidTicket(datosOrigenDevolucionFalsos.getUidTicket());
									docOrigen.setCodTicket(datosOrigenDevolucionFalsos.getCodTicket());

									ticketManager.getTicket().getCabecera().setDatosDocOrigen(docOrigen);

								}
							}
							
							ticketManager.getTicket().getCabecera().setCodTipoDocumento(tipoDocumentoActivo.getCodtipodocumento());
							ticketManager.getTicket().setEsDevolucion(true);
							
							DatosDocumentoOrigenTicket datosDocOrigen = ticketManager.getTicket().getCabecera().getDatosDocOrigen();
							ticketManager.setTicketOrigenFalso(datosDocOrigen.getCodTicket(), datosDocOrigen.getTienda(), datosDocOrigen.getCaja(), datosDocOrigen.getIdTipoDoc());
						}
						catch (Exception e) {
							log.error("clearBackupReturns() - Ha habido un error al recuperar el ticket: " + e.getMessage(), e);
						}
					}	
				
					ticketManager.salvarTicketVacio();
					
				}
				
				ticketAparcadoMapper.deleteByPrimaryKey(copiaSeguridad);
			}
		}
		catch (Exception e) {
			log.error("clearBackupReturns() - Error: " + e.getMessage(), e);
		}finally {
			try {
				ticketManager.inicializarTicket();
				ticketManager.setTicketOrigen(null);
				
			} catch (DocumentoException | PromocionesServiceException e) {
				log.error("clearBackupReturns() - Ha habido un error al inicializar el ticket: " + e.getMessage(), e);

			}
		}
	}

}
