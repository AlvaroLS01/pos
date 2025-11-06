package com.comerzzia.dinosol.pos.services.ticket.aparcados;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.core.servicios.variables.Variables;
import com.comerzzia.core.util.numeros.BigDecimalUtil;
import com.comerzzia.dinosol.pos.services.auditorias.AuditoriaDto;
import com.comerzzia.dinosol.pos.services.auditorias.AuditoriasService;
import com.comerzzia.dinosol.pos.services.core.sesion.DinoSesionAplicacion;
import com.comerzzia.pos.persistence.tickets.aparcados.TicketAparcadoBean;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.ticket.TicketVenta;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.services.ticket.TicketsServiceException;
import com.comerzzia.pos.services.ticket.aparcados.TicketsAparcadosService;
import com.comerzzia.pos.util.i18n.I18N;
import com.comerzzia.pos.util.xml.MarshallUtil;

@Component
@Primary
public class DinoTicketsAparcadosService extends TicketsAparcadosService {

	public static final String ID_VARIABLE_SINCRONIZACION_TICKET_APARCADO = "X_APARCADOS.SINCRONIZACION";

	private Logger log = Logger.getLogger(DinoTicketsAparcadosService.class);

	@Autowired
	private VariablesServices variablesServices;

	@Autowired
	private Sesion sesion;

	@Autowired
	private ClienteTicketsAparcadosIse clienteTicketsAparcadosIse;

	@Autowired
	private AuditoriasService auditoriasService;

	@Override
	public List<TicketAparcadoBean> consultarTickets(String uidTicket, Date fecha, String usuario, Long idTipoDoc) throws TicketsServiceException {
		if (!isTicketAparcadoRemotoActivo()) {
			return super.consultarTickets(uidTicket, fecha, usuario, idTipoDoc);
		}
		else {
			try {
				String apiKey = variablesServices.getVariableAsString(Variables.WEBSERVICES_APIKEY);
				List<TicketAparcadoDto> tickets = clienteTicketsAparcadosIse.consultarTicketsAparcados(sesion.getAplicacion().getUidActividad(), sesion.getAplicacion().getCodAlmacen(), apiKey);

				List<TicketAparcadoBean> ticketsAparcados = new ArrayList<TicketAparcadoBean>();
				if (tickets != null && !tickets.isEmpty()) {
					for (TicketAparcadoDto ticket : tickets) {
						TicketAparcadoBean ticketAparcado = new TicketAparcadoBean();
						BeanUtils.copyProperties(ticketAparcado, ticket);
						ticketAparcado.setTicket(ticket.getTicket().getBytes());
						ticketsAparcados.add(ticketAparcado);
					}
				}
				return ticketsAparcados;
			}
			catch (Exception e) {
				log.error("consultarTickets() - Ha habido un error al consultar los tickets aparcados en la caja máster: " + e.getMessage(), e);

				throw new TicketsServiceException(e);
			}
		}
	}

	@Override
	public int countTicketsAparcados(Long idTipoDocumento) {
		if (!isTicketAparcadoRemotoActivo()) {
			return super.countTicketsAparcados(idTipoDocumento);
		}
		else {
			log.debug("countTicketsAparcados() - Consultando número de tickets aparcados en caja máster.");
			List<TicketAparcadoBean> tickets = new ArrayList<TicketAparcadoBean>();;
			try {
				tickets = consultarTickets(null, null, null, idTipoDocumento);
			}
			catch (TicketsServiceException e) {
				log.trace("countTicketsAparcados() - Ha habido un error al consultar los tickets aparcados en la caja máster: " + e.getMessage(), e);
			}
			return tickets.size();
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void aparcarTicket(TicketVenta ticket) throws TicketsServiceException {
		if (ticket.getIdTicket() != null) {
			throw new TicketsServiceException(null, new TicketConIdAparcadoException());
		}

		if (!isTicketAparcadoRemotoActivo()) {
			super.aparcarTicket(ticket);
		}
		else {
			try {
				String apiKey = variablesServices.getVariableAsString(Variables.WEBSERVICES_APIKEY);

				TicketAparcadoDto dto = new TicketAparcadoDto();

				dto.setApiKey(apiKey);

				byte[] xmlTicket = MarshallUtil.crearXML(ticket);
				log.debug("TICKET: " + ticket.getUidTicket() + "\n" + new String(xmlTicket, "UTF-8") + "\n");

				dto.setTicket(new String(xmlTicket));
				dto.setCodAlmacen(ticket.getTienda().getCodAlmacen());
				dto.setUidTicket(ticket.getUidTicket());
				dto.setUidActividad(sesion.getAplicacion().getUidActividad());
				dto.setUsuario(ticket.getCajero().getUsuario());
				dto.setCodCaja(ticket.getCodCaja());
				if (ticket.getCliente() != null) {
					dto.setCodCliente(ticket.getCliente().getCodCliente());
				}
				dto.setNumArticulos(ticket.getLineas().size());
				dto.setImporte(ticket.getTotales().getTotal());
				dto.setIdTipoDocumento(ticket.getCabecera().getTipoDocumento());

				clienteTicketsAparcadosIse.aparcarTicket(dto);
			}
			catch (TicketsMaximoAparcadosException e1) {
				String mensaje = I18N.getTexto("No se pueden aparcar más de dos tickets por cajero el mismo día.");
				throw new TicketsServiceException(mensaje, e1);
			}
			catch (Exception e) {
				log.error("aparcarTicket() - Ha habido un error al aparcar el ticket en la caja máster: " + e.getMessage(), e);

				throw new TicketsServiceException(e);
			}
		}

		AuditoriaDto auditoria = new AuditoriaDto();
		auditoria.setTipo("APARCAR TICKET");
		auditoria.setUidTicket(ticket.getUidTicket());
		if (ticket.getCabecera().getDatosFidelizado() != null) {
			auditoria.setNumTarjetaFidelizado(ticket.getCabecera().getDatosFidelizado().getNumTarjetaFidelizado());
			auditoria.setNombreFidelizado(ticket.getCabecera().getDatosFidelizado().getNombre());
		}
		
		BigDecimal totalAPagar = ticket.getTotales().getTotalAPagar();
		if(BigDecimalUtil.isMayor(totalAPagar, new BigDecimal(99999.99))) {
			totalAPagar = new BigDecimal(99999.99);
			log.warn("aparcarTicket() - El importe del ticket es excesivo, revisar la introducción de los artículos de la venta.");
		}
		auditoria.setImporteTicket(totalAPagar);
		
		BigDecimal cantidadArticulos = ((TicketVentaAbono)ticket).getCabecera().getCantidadArticulos();
		if(BigDecimalUtil.isMayor(cantidadArticulos, new BigDecimal(99999))) {
			cantidadArticulos = new BigDecimal(99999);
			log.warn("aparcarTicket() - El número de artículos del ticket es excesivo, revisar la introducción de los artículos de la venta.");
		}
		auditoria.setNumArticulosTicket(cantidadArticulos);
		
		auditoriasService.guardarAuditoria(auditoria);
	}

	@Override
	public void eliminarTicket(String uidTicket) throws TicketsServiceException {
		if (!isTicketAparcadoRemotoActivo()) {
			super.eliminarTicket(uidTicket);
		}
		else {
			try {
				String apiKey = variablesServices.getVariableAsString(Variables.WEBSERVICES_APIKEY);
				clienteTicketsAparcadosIse.eliminarTicketAparcado(sesion.getAplicacion().getUidActividad(), sesion.getAplicacion().getCodAlmacen(), apiKey, uidTicket);
			}
			catch (Exception e) {
				log.error("eliminarTicket() - Ha habido un error al eliminar el ticket en la caja máster: " + e.getMessage(), e);

				throw new TicketsServiceException(e);
			}
		}
	}

	public boolean isTicketAparcadoRemotoActivo() {
		Boolean sincronizacionTicketAparcadoMaster = variablesServices.getVariableAsBoolean(ID_VARIABLE_SINCRONIZACION_TICKET_APARCADO);
		boolean cajaMasterActivada = ((DinoSesionAplicacion) sesion.getAplicacion()).isCajaMasterActivada();
		boolean ticketAparcadoRemoto = cajaMasterActivada && sincronizacionTicketAparcadoMaster;
		return ticketAparcadoRemoto;
	}

}
