package com.comerzzia.pos.services.ticket.copiaSeguridad;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.pos.persistence.cajas.movimientos.CajaMovimientoBean;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.persistence.tickets.aparcados.TicketAparcadoBean;
import com.comerzzia.pos.persistence.tickets.aparcados.TicketAparcadoExample;
import com.comerzzia.pos.persistence.tickets.aparcados.TicketAparcadoMapper;
import com.comerzzia.pos.services.cajas.CajasService;
import com.comerzzia.pos.services.core.documentos.Documentos;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.ticket.TicketVenta;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.services.ticket.TicketsService;
import com.comerzzia.pos.services.ticket.TicketsServiceException;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.pagos.PagoTicket;
import com.comerzzia.pos.services.ticket.profesional.TicketVentaProfesional;
import com.comerzzia.pos.services.ticket.profesional.TotalesTicketProfesional;
import com.comerzzia.pos.util.xml.MarshallUtil;
import com.comerzzia.pos.util.xml.MarshallUtilException;

@SuppressWarnings("deprecation")
@Service
public class CopiaSeguridadTicketService {

	protected static final Logger log = Logger.getLogger(CopiaSeguridadTicketService.class);

	public static final String USUARIO_BACKUP_TICKET = "****";

	@Autowired
	protected Sesion sesion;

	@Autowired
	protected TicketAparcadoMapper ticketAparcadoMapper;
	
	@Autowired
	private Documentos documentos;
	
	@Autowired
	private CajasService cajasService;
	
	@Autowired
	private TicketsService ticketsService;

	/**
	 * Devuelve el ticket aparcado marcado como copia de seguridad (usuario igual a CopiaSeguridadTicketService.USUARIO_BACKUP_TICKET) para el tipo de documento indicado
	 * y la caja actual
	 * @param tipoDocumentoActivo
	 * @return TicketAparcadoBean
	 * @throws TicketsServiceException
	 */
	public TicketAparcadoBean consultarCopiaSeguridadTicket(TipoDocumentoBean tipoDocumentoActivo) throws TicketsServiceException {
		SqlSession sqlSession = new SqlSession();
		TicketAparcadoBean res = null;
		try {
			log.debug("consultarCopiaSeguridadTicket() - Consultando copia de seguridad de ticket en base de datos...");
			sqlSession.openSession(SessionFactory.openSession());
			TicketAparcadoExample example = new TicketAparcadoExample();
			TicketAparcadoExample.Criteria criteria = example.createCriteria();
			criteria.andUidActividadEqualTo(sesion.getAplicacion().getUidActividad());
			criteria.andCodAlmacenEqualTo(sesion.getAplicacion().getCodAlmacen());
			criteria.andCodCajaEqualTo(sesion.getAplicacion().getCodCaja());
			criteria.andUsuarioEqualTo(USUARIO_BACKUP_TICKET);
			criteria.andIdTipoDocumentoEqualTo(tipoDocumentoActivo.getIdTipoDocumento());
			example.or(criteria);
			
			// Si es una factura simplificada es posible que haya una copia de seguridad de una factura completa
			if(tipoDocumentoActivo.getCodtipodocumento().equals(Documentos.FACTURA_SIMPLIFICADA)) {
				TicketAparcadoExample.Criteria criteriaAux = example.createCriteria();
				criteriaAux.andUidActividadEqualTo(sesion.getAplicacion().getUidActividad());
				criteriaAux.andCodAlmacenEqualTo(sesion.getAplicacion().getCodAlmacen());
				criteriaAux.andCodCajaEqualTo(sesion.getAplicacion().getCodCaja());
				criteriaAux.andIdTipoDocumentoEqualTo(sesion.getAplicacion().getDocumentos().getDocumento(Documentos.FACTURA_COMPLETA).getIdTipoDocumento());
				criteriaAux.andUsuarioEqualTo(USUARIO_BACKUP_TICKET);
					
				example.or(criteriaAux);
			}
			
			List<TicketAparcadoBean> resultados = ticketAparcadoMapper.selectByExampleWithBLOBs(example);
			if (resultados != null && !resultados.isEmpty()) {
				if(resultados.size() == 1) {
					res = resultados.get(0);
				}
				else {
					// En este caso se debe a que existe una copia de la factura simplificada y una de la factura completa, por lo que buscamos la factura completa
					for(TicketAparcadoBean resultado : resultados) {
						if(resultado.getIdTipoDocumento().equals(sesion.getAplicacion().getDocumentos().getDocumento(Documentos.FACTURA_COMPLETA))) {
							res = resultado;
							break;
						}
					}
				}
			}
			return res;

		}
		catch (Exception e) {
			String msg = "Se ha producido un error consultando copia de seguridad de ticket en base de datos para tipo de documento: " + tipoDocumentoActivo.getIdTipoDocumento();
			log.error("consultarCopiaSeguridadTicket() - " + msg, e);
			throw new TicketsServiceException(e);
		}
		finally {
			sqlSession.close();
		}
	}

	/**
	 * Guardar una copia de seguridad del ticket activo, eliminando la anterior copia de seguridad en caso de que
	 * existiese.
	 * 
	 * @param ticket
	 * @throws TicketsServiceException
	 */
	@SuppressWarnings("rawtypes")
	public synchronized void guardarBackupTicketActivo(TicketVenta ticket) throws TicketsServiceException {
		log.debug("guardarBackupTicketActivo()");
		SqlSession sqlSession = new SqlSession();

		try {
			sqlSession.openSession(SessionFactory.openSession());

			log.debug("guardarBackupTicketActivo() - Eliminando copia de seguridad de ticket...");

			TicketAparcadoExample example = new TicketAparcadoExample();
			TicketAparcadoExample.Criteria criteria = example.createCriteria();
			criteria.andUidActividadEqualTo(sesion.getAplicacion().getUidActividad());
			criteria.andCodAlmacenEqualTo(sesion.getAplicacion().getCodAlmacen());
			criteria.andCodCajaEqualTo(sesion.getAplicacion().getCodCaja());
			criteria.andIdTipoDocumentoEqualTo(ticket.getCabecera().getTipoDocumento());
			criteria.andUsuarioEqualTo(USUARIO_BACKUP_TICKET);
			example.or(criteria);
			
			// Si es de tipo factura simplificada borramos también la posible factura completa persistida
			if(ticket.getCabecera().getCodTipoDocumento() != null && ticket.getCabecera().getCodTipoDocumento().equals(Documentos.FACTURA_SIMPLIFICADA)) {
				TicketAparcadoExample.Criteria criteriaAux = example.createCriteria();
				criteriaAux.andUidActividadEqualTo(sesion.getAplicacion().getUidActividad());
				criteriaAux.andCodAlmacenEqualTo(sesion.getAplicacion().getCodAlmacen());
				criteriaAux.andCodCajaEqualTo(sesion.getAplicacion().getCodCaja());
				criteriaAux.andIdTipoDocumentoEqualTo(sesion.getAplicacion().getDocumentos().getDocumento(Documentos.FACTURA_COMPLETA).getIdTipoDocumento());
				criteriaAux.andUsuarioEqualTo(USUARIO_BACKUP_TICKET);
					
				example.or(criteriaAux);
			}
			
			// Si es de tipo factura completa borramos también la posible factura simplificada persistida
			if(ticket.getCabecera().getCodTipoDocumento() != null && ticket.getCabecera().getCodTipoDocumento().equals(Documentos.FACTURA_COMPLETA)) {
				TicketAparcadoExample.Criteria criteriaAux = example.createCriteria();
				criteriaAux.andUidActividadEqualTo(sesion.getAplicacion().getUidActividad());
				criteriaAux.andCodAlmacenEqualTo(sesion.getAplicacion().getCodAlmacen());
				criteriaAux.andCodCajaEqualTo(sesion.getAplicacion().getCodCaja());
				criteriaAux.andIdTipoDocumentoEqualTo(sesion.getAplicacion().getDocumentos().getDocumento(Documentos.FACTURA_SIMPLIFICADA).getIdTipoDocumento());
				criteriaAux.andUsuarioEqualTo(USUARIO_BACKUP_TICKET);
					
				example.or(criteriaAux);
			}
			
			ticketAparcadoMapper.deleteByExample(example);
//			TODO Se restringe el aparcado de tickets a TicketVentaAbono y TicketVentaProfesional. Habría que reimplemntar el recuperar ticket aparcado de apartados
			if(ticket instanceof TicketVentaAbono || ticket instanceof TicketVentaProfesional){
				if (ticket.getCabecera().getIdTicket() != null || (ticket.getLineas() != null && !ticket.getLineas().isEmpty())) {
					log.debug("guardarBackupTicketActivo() - Guardando copia de seguridad de ticket...");

					TicketAparcadoBean ta = prepararTicketAparcado(ticket);
					ta.setUsuario(USUARIO_BACKUP_TICKET);
					ticketAparcadoMapper.insert(ta);
				}
			}
			

			log.debug("guardarBackupTicketActivo() - Confirmando transacción...");
			sqlSession.commit();
		}
		catch (Exception e) {
			sqlSession.rollback();
			String msg = "Se ha producido un error eliminando la copia de seguridad del ticket: " + e.getMessage();
			log.error("guardarBackupTicketActivo() - " + msg, e);
			throw new TicketsServiceException(e);
		}
		finally {
			sqlSession.close();
		}
	}

	@SuppressWarnings("rawtypes")
	public synchronized TicketAparcadoBean prepararTicketAparcado(TicketVenta ticket) throws MarshallUtilException, UnsupportedEncodingException {
		TicketAparcadoBean ta = new TicketAparcadoBean();
		// Seteamos los valores para el nuevo bean
		// Obtenemos el xml del ticket
		List<Class<?>> clasesAux = new ArrayList<Class<?>>();
		clasesAux.add(TotalesTicketProfesional.class);
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
	
	public void eliminarBackup(SqlSession sqlSession, String uidTicket) throws TicketsServiceException{

    	try{
    		log.debug("eliminarBackup() - Eliminando backup del ticket en base de datos...");
            
            TicketAparcadoExample example = new TicketAparcadoExample();
            TicketAparcadoExample.Criteria criteria = example.createCriteria();
            criteria.andUidTicketEqualTo(uidTicket);                   
            example.or(criteria);
            ticketAparcadoMapper.deleteByExample(example);
            
            log.debug("eliminarBackup() - Backup del ticket eliminado correctamente.");
    	}
    	 catch (Exception e) {
             String msg = "Se ha producido un error eliminando el backup del ticket con uid " + uidTicket + " : " + e.getMessage();
             log.error("eliminarBackup() - " + msg, e);
             throw new TicketsServiceException(e);
         }
                  
       
    }

	public boolean existsBackup() throws TicketsServiceException {
		try {
			log.debug("existsBackup() - Checking if exists backup.");
			TicketAparcadoExample example = new TicketAparcadoExample();
			TicketAparcadoExample.Criteria criteria = example.createCriteria();
			criteria.andUidActividadEqualTo(sesion.getAplicacion().getUidActividad());
			criteria.andCodAlmacenEqualTo(sesion.getAplicacion().getCodAlmacen());
			criteria.andCodCajaEqualTo(sesion.getAplicacion().getCodCaja());
			criteria.andUsuarioEqualTo(USUARIO_BACKUP_TICKET);
			example.or(criteria);
			
			
			List<TicketAparcadoBean> resultados = ticketAparcadoMapper.selectByExampleWithBLOBs(example);
			if (resultados != null && !resultados.isEmpty()) {
				return true;
			}
			
			return false;
		}
		catch (Exception e) {
			log.error("existsBackup() - Error while check backup: " + e.getMessage(), e);
			throw new TicketsServiceException(e);
		}
    }

	public void clearBackupReturns() {
		try {
			TicketAparcadoBean ticketBackup = consultarCopiaSeguridadTicket(documentos.getDocumento("NC"));
			if(ticketBackup != null) {
				TicketVentaAbono ticket = (TicketVentaAbono) MarshallUtil.leerXML(ticketBackup.getTicket(), TicketVentaAbono.class);
				if(ticket.getPagos() != null) {
					for(PagoTicket pago : ticket.getPagos()) {
						if(pago.isMovimientoCajaInsertado()) {
							CajaMovimientoBean movimiento = new CajaMovimientoBean();
							movimiento.setFecha(new Date());
						    
					    	movimiento.setCargo(pago.getImporte().abs());
					    	movimiento.setAbono(BigDecimal.ZERO);
						    
						    movimiento.setConcepto(ticket.getCabecera().getDesTipoDocumento() + ": " + ticket.getCabecera().getCodTicket());
						    movimiento.setDocumento(ticket.getCabecera().getCodTicket());
						    movimiento.setCodMedioPago(pago.getCodMedioPago());
						    movimiento.setIdDocumento(ticket.getUidTicket());
						    movimiento.setIdTipoDocumento(ticket.getCabecera().getTipoDocumento());
						    
						    cajasService.crearMovimiento(movimiento);
						}
					}
				}
				
				ticket.getCabecera().setTicket(ticket);
				for(LineaTicket linea : ticket.getLineas()) {
					linea.setCabecera(ticket.getCabecera());
				}
				
				ticketsService.saveEmptyTicket(ticket, documentos.getDocumento(ticket.getCabecera().getTipoDocumento()), sesion.getAplicacion().getDocumentos().getDocumento(Documentos.FACTURA_SIMPLIFICADA));
				ticketAparcadoMapper.deleteByPrimaryKey(ticketBackup);
			}
		}
		catch (Exception e) {
			log.error("clearBackupReturns() - Error: " + e.getMessage(), e);
		}
	}

}
