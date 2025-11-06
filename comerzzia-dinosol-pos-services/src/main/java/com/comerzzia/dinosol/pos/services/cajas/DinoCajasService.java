package com.comerzzia.dinosol.pos.services.cajas;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.core.servicios.variables.Variables;
import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.dinosol.pos.services.cajas.movimientos.MovimientoProvisionalBean;
import com.comerzzia.dinosol.pos.services.core.sesion.DinoSesionCaja;
import com.comerzzia.instoreengine.master.rest.client.cajas.CajaRequestRest;
import com.comerzzia.instoreengine.master.rest.client.cajas.CajaResponseRest;
import com.comerzzia.instoreengine.master.rest.client.cajas.CajasRest;
import com.comerzzia.model.ventas.cajas.CajaDTO;
import com.comerzzia.model.ventas.cajas.cabecera.CabeceraCaja;
import com.comerzzia.model.ventas.cajas.detalle.DetalleCaja;
import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.persistence.cajas.CajaBean;
import com.comerzzia.pos.persistence.cajas.CajaExample;
import com.comerzzia.pos.persistence.cajas.CajaKey;
import com.comerzzia.pos.persistence.cajas.conceptos.CajaConceptoBean;
import com.comerzzia.pos.persistence.cajas.movimientos.CajaMovimientoBean;
import com.comerzzia.pos.persistence.cajas.movimientos.CajaMovimientoExample;
import com.comerzzia.pos.persistence.core.usuarios.UsuarioBean;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.persistence.mybatis.SpringTransactionSqlSession;
import com.comerzzia.pos.persistence.tickets.POSTicketMapper;
import com.comerzzia.pos.persistence.tickets.TicketBean;
import com.comerzzia.pos.services.cajas.Caja;
import com.comerzzia.pos.services.cajas.CajaEstadoException;
import com.comerzzia.pos.services.cajas.CajasService;
import com.comerzzia.pos.services.cajas.CajasServiceException;
import com.comerzzia.pos.services.core.contadores.ContadorServiceException;
import com.comerzzia.pos.services.core.contadores.ServicioContadores;
import com.comerzzia.pos.services.core.sesion.SesionAplicacion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.mediospagos.MediosPagosService;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;
import com.comerzzia.pos.util.xml.MarshallUtil;
import com.comerzzia.pos.util.xml.MarshallUtilException;
import com.comerzzia.rest.client.exceptions.RestException;
import com.comerzzia.rest.client.exceptions.RestHttpException;

@Component
@Primary
@SuppressWarnings("deprecation")
public class DinoCajasService extends CajasService {

	private Logger log = Logger.getLogger(DinoCajasService.class);
	
	private static final String ID_CONTADOR_MOV_PROVISIONALES = "ID_MOVCAJPROV";

	@Autowired
	private SesionAplicacion sesionAplicacion;

	@Autowired
	private VariablesServices variablesServices;
	
	@Autowired
	private ServicioContadores contadoresService;
	
	@Autowired
	private POSTicketMapper ticketMapper;

	public Caja pedirYGuardarCajaAbierta(UsuarioBean usuario) throws Exception {
		log.debug("pedirYGuardarCajaAbierta() - Solicitando caja para el usuario : " + usuario.getUsuario());

		try {
			String apiKey = variablesServices.getVariableAsString(Variables.WEBSERVICES_APIKEY);

			CajaDTO cajaDto = pedirCajaEnMaster(usuario);
			if (cajaDto != null) {
				if(cajaDto.getCabecera() != null) {
					CajaRequestRest requestTransferida = new CajaRequestRest();
					requestTransferida.setUidActividad(sesionAplicacion.getUidActividad());
					requestTransferida.setApiKey(apiKey);
					requestTransferida.setCodcaja(sesionAplicacion.getCodCaja());
					requestTransferida.setUidDiarioCaja(cajaDto.getCabecera().getUidDiarioCaja());
					try {
						CajasRest.cajaTransferidaPOS(requestTransferida);
					}
					catch (Exception e) {
						log.error("pedirYGuardarCajaAbierta() - Ha ocurrido un error al marcar la caja como transferida: " + e.getMessage(), e);
						return null;
					}
					Caja caja = null;
					try {
						caja = guardarCaja(cajaDto);
					}
					catch (Exception e) {
						log.error("pedirYGuardarCajaAbierta() - Ha ocurrido un error al salvar la caja. Se llamará al servicio REST para volver a marcar la caja como transferida al master", e);
						requestTransferida.setCodcaja(cajaDto.getCabecera().getCodcaja());
						CajasRest.cajaTransferidaMaster(requestTransferida);
					}
					return caja;
				}
				else {
					return new Caja();
				}
			}
			else {
				log.debug("pedirYGuardarCajaAbierta() - El servicio REST de la caja máster no ha devuelto una caja.");
				return null;
			}
		}
		catch (Exception e) {
			log.error("pedirYGuardarCajaAbierta() - Ha habido un error al recuperar la caja de la caja máster: " + e.getMessage(), e);
			throw e;
		}
	}

	public CajaDTO pedirCajaEnMaster(UsuarioBean usuario) throws RestException, RestHttpException {
		String apiKey = variablesServices.getVariableAsString(Variables.WEBSERVICES_APIKEY);
		
	    CajaRequestRest request = new CajaRequestRest();
	    request.setUidActividad(sesionAplicacion.getUidActividad());
	    request.setApiKey(apiKey);
	    request.setCodalm(sesionAplicacion.getCodAlmacen());
	    request.setCodcaja(sesionAplicacion.getCodCaja());
	    request.setUsuario(usuario.getUsuario());

	    CajaResponseRest cajaDto = CajasRest.recuperarCaja(request);
	    
	    if(cajaDto == null) {
	    	return null;
	    }
	    
	    return cajaDto.getCaja();
    }

	private Caja guardarCaja(CajaDTO cajaDto) {
		SqlSession sqlSession = SpringContext.getBean(SpringTransactionSqlSession.class);
		try {
			sqlSession.openSession(SessionFactory.openSession());

			CajaBean cajaBean = new CajaBean();
			cajaBean.setUidActividad(cajaDto.getCabecera().getUidActividad());
			cajaBean.setUidDiarioCaja(cajaDto.getCabecera().getUidDiarioCaja());
			cajaBean.setCodAlmacen(cajaDto.getCabecera().getCodalm());
			cajaBean.setCodCaja(sesionAplicacion.getCodCaja());
			cajaBean.setUsuario(cajaDto.getCabecera().getUsuario());
			cajaBean.setFechaApertura(cajaDto.getCabecera().getFechaApertura());
			cajaBean.setFechaCierre(cajaDto.getCabecera().getFechaCierre());
			cajaBean.setUsuarioCierre(cajaDto.getCabecera().getUsuarioCierre());
			cajaBean.setFechaEnvio(cajaDto.getCabecera().getFechaEnvio());

			CajaKey cajaKey = new CajaKey();
			cajaKey.setUidActividad(sesionAplicacion.getUidActividad());
			cajaKey.setUidDiarioCaja(cajaDto.getCabecera().getUidDiarioCaja());
			if (cajaMapper.selectByPrimaryKey(cajaKey) == null) {
				log.debug("guardarCaja() - Insertando caja con codAlmacen " + cajaDto.getCabecera().getCodalm() + "  y codCaja " + cajaDto.getCabecera().getCodcaja());

				// Dependiendo si la fecha tiene hora o no, llamamos a un método u otro del mapper
				Calendar calendarApertura = Calendar.getInstance();
				calendarApertura.setTime(cajaDto.getCabecera().getFechaApertura());
				if (calendarApertura.get(Calendar.HOUR_OF_DAY) == 0 && calendarApertura.get(Calendar.MINUTE) == 0 && calendarApertura.get(Calendar.SECOND) == 0
				        && calendarApertura.get(Calendar.MILLISECOND) == 0) {
					cajaMapper.insertFechaAperturaDate(cajaBean);
				}
				else {
					cajaMapper.insertFechaAperturaDateTime(cajaBean);
				}
			}
			else {
				CajaMovimientoExample movExample = new CajaMovimientoExample();
				movExample.or().andUidActividadEqualTo(sesionAplicacion.getUidActividad()).andUidDiarioCajaEqualTo(cajaDto.getCabecera().getUidDiarioCaja());
				cajaMovimientoMapper.deleteByExample(movExample);

				cajaMapper.updateByPrimaryKey(cajaBean);
			}

			if (cajaDto.getDetalles() != null) {
				for (DetalleCaja detalle : cajaDto.getDetalles()) {
					CajaMovimientoBean movimiento = new CajaMovimientoBean();
					movimiento.setUidActividad(detalle.getUidActividad());
					movimiento.setUidDiarioCaja(detalle.getUidDiarioCaja());
					movimiento.setLinea(detalle.getLinea());
					movimiento.setFecha(detalle.getFecha());
					movimiento.setUidTransaccionDet(detalle.getUidTransaccionDet());
					movimiento.setAbono(detalle.getAbono());
					movimiento.setCargo(detalle.getCargo());
					movimiento.setCodConceptoMovimiento(detalle.getCodconceptoMov());
					movimiento.setCoddivisa(detalle.getCoddivisa());
					movimiento.setCodMedioPago(detalle.getCodmedpag());
					movimiento.setConcepto(detalle.getConcepto());
					movimiento.setDocumento(detalle.getDocumento());
					movimiento.setIdDocumento(detalle.getIdDocumento());
					movimiento.setIdTipoDocumento(detalle.getIdTipoDocumento());
					movimiento.setTipoDeCambio(detalle.getTipoDeCambio());
					movimiento.setUidTransaccionDet(detalle.getUidTransaccionDet());
					movimiento.setUsuario(detalle.getUsuario());
					cajaMovimientoMapper.insert(movimiento);
				}
			}

			sqlSession.commit();
			return new Caja(cajaBean);
		}
		catch (Exception e) {
			log.error("guardarCaja() - Se ha producido un error insertando caja:" + e.getMessage(), e);
			sqlSession.rollback();
			throw e;
		}
		finally {
			sqlSession.close();
		}
	}

	public void transferirCaja(Caja caja) throws RestException, RestHttpException, CajasServiceException {
		String apiKey = variablesServices.getVariableAsString(Variables.WEBSERVICES_APIKEY);

		CajaRequestRest request = new CajaRequestRest();
		request.setUidActividad(sesionAplicacion.getUidActividad());
		request.setApiKey(apiKey);
		request.setUidDiarioCaja(caja.getUidDiarioCaja());
		CajaDTO cajaDTO = new CajaDTO();
		CabeceraCaja cabecera = new CabeceraCaja();
		cabecera.setCodalm(caja.getCodAlm());
		cabecera.setCodcaja(caja.getCodCaja());
		cabecera.setFechaApertura(caja.getFechaApertura());
		cabecera.setFechaCierre(caja.getFechaCierre());
		cabecera.setFechaContable(caja.getFechaContable());
		cabecera.setUidActividad(sesion.getAplicacion().getUidActividad());
		cabecera.setUidDiarioCaja(caja.getUidDiarioCaja());
		cabecera.setUsuario(caja.getUsuario());
		cajaDTO.setCabecera(cabecera);
		consultarMovimientos(caja);
		for (CajaMovimientoBean movimiento : caja.getMovimientos()) {
			DetalleCaja detalle = new DetalleCaja();
			detalle.setAbono(movimiento.getAbono());
			detalle.setCargo(movimiento.getCargo());
			detalle.setCodconceptoMov(movimiento.getCodConceptoMovimiento());
			detalle.setCoddivisa(movimiento.getCoddivisa());
			detalle.setCodmedpag(movimiento.getCodMedioPago());
			detalle.setConcepto(movimiento.getConcepto());
			detalle.setDocumento(movimiento.getDocumento());
			detalle.setFecha(movimiento.getFecha());
			detalle.setIdDocumento(movimiento.getIdDocumento());
			detalle.setIdTipoDocumento(movimiento.getIdTipoDocumento());
			detalle.setLinea(movimiento.getLinea());
			detalle.setTipoDeCambio(movimiento.getTipoDeCambio());
			detalle.setUidActividad(movimiento.getUidActividad());
			detalle.setUidDiarioCaja(movimiento.getUidDiarioCaja());
			detalle.setUidTransaccionDet(movimiento.getUidTransaccionDet());
			detalle.setUsuario(movimiento.getUsuario());
			cajaDTO.getDetalles().add(detalle);
		}
		CajasRest.transferirCaja(request, cajaDTO);
	}

	public void marcarCajaTransferida(CajaBean caja) throws Exception {
		String apiKey = variablesServices.getVariableAsString(Variables.WEBSERVICES_APIKEY);
		SqlSession sqlSession = SpringContext.getBean(SpringTransactionSqlSession.class);
		try {
			sqlSession.openSession(SessionFactory.openSession());

			CajaMovimientoExample detalleExample = new CajaMovimientoExample();
			detalleExample.or().andUidActividadEqualTo(sesion.getAplicacion().getUidActividad()).andUidDiarioCajaEqualTo(caja.getUidDiarioCaja());
			cajaMovimientoMapper.deleteByExample(detalleExample);
			
			cajaMapper.deleteByPrimaryKey(caja);

			CajaRequestRest request = new CajaRequestRest();
			request.setApiKey(apiKey);
			request.setUidActividad(sesion.getAplicacion().getUidActividad());
			request.setUidDiarioCaja(caja.getUidDiarioCaja());
			request.setCodcaja(caja.getCodCaja());
			CajasRest.cajaTransferidaMaster(request);
			
			sqlSession.commit();
			
			((DinoSesionCaja) sesion.getSesionCaja()).setCajaAbierta(null);
		}
		catch (Exception e) {
			log.error("cajaTransferida() - Se ha producido un error insertando caja:" + e.getMessage(), e);
			sqlSession.rollback();
			throw e;
		}
		finally {
			sqlSession.close();
		}
	}

	@Override
	public Caja consultarCajaAbierta() throws CajasServiceException, CajaEstadoException {
		try {
			CajaExample exampleCaja = new CajaExample();
			exampleCaja.or().andUidActividadEqualTo(sesion.getAplicacion().getUidActividad()).andCodAlmacenEqualTo(sesion.getAplicacion().getCodAlmacen())
			        .andCodcajaEqualTo(sesion.getAplicacion().getCodCaja()).andFechaCierreIsNull().andFechaEnvioIsNull();
			log.debug("consultarCajaAbierta() - Consultado caja abierta en sesion");
			List<CajaBean> cajasBean = cajaMapper.selectByExample(exampleCaja);

			if (cajasBean.isEmpty()) {
				throw new CajaEstadoException(I18N.getTexto("No existe caja abierta en el sistema"));
			}
			return new Caja(cajasBean.get(0));
		}
		catch (CajaEstadoException e) {
			throw e;
		}
		catch (Exception e) {
			String msg = "Se ha producido un error consultando caja abierta en sesion :" + e.getMessage();
			log.error("consultarCajaAbiertaNoTransferida() - " + msg, e);
			throw new CajasServiceException(I18N.getTexto("Error al consultar caja abierta en sesión del sistema"), e);
		}
	}
	
	@Override
	public CajaMovimientoBean crearMovimientoManual(BigDecimal importe, String codConcepto, String documento, String descConcepto) throws CajasServiceException {
		log.debug("crearMovimientoManual() - Registrando movimiento manual por importe: " + importe + ". Y concepto: " + codConcepto);
		SqlSession sqlSession = SpringContext.getBean(SpringTransactionSqlSession.class);
		CajaMovimientoBean movimiento = new CajaMovimientoBean();
		try {
			sqlSession.openSession(SessionFactory.openSession());

			CajaConceptoBean concepto = cajaConceptosServices.getConceptoCaja(codConcepto);
			if (concepto == null) {
				log.error("crearMovimientoManual() - Se está intentando insertar un movimiento con concepto nulo. Código concepto: " + codConcepto);
				throw new CajasServiceException(I18N.getTexto("Error al insertar movimiento de caja"));
			}
			concepto.setDesConceptoMovimiento(descConcepto);
			if (CajaConceptoBean.MOV_ENTRADA.equals(concepto.getInOut())) {
				movimiento.setCargo(importe);
			}
			else if (CajaConceptoBean.MOV_SALIDA.equals(concepto.getInOut())) {
				movimiento.setAbono(importe);
			}
			else {
				if (BigDecimalUtil.isMayorACero(importe)) {
					movimiento.setAbono(importe); // salida de caja
				}
				else {
					movimiento.setCargo(importe.negate()); // entrada de caja
				}
			}
			movimiento.setCodConceptoMovimiento(concepto.getCodConceptoMovimiento());
			movimiento.setConcepto(concepto.getDesConceptoMovimiento());
			movimiento.setCodMedioPago(MediosPagosService.medioPagoDefecto.getCodMedioPago());
			movimiento.setDocumento(documento);
			movimiento.setFecha(new Date());
			movimiento.setUsuario(sesion.getSesionUsuario().getUsuario().getUsuario());

			crearMovimiento(sqlSession, movimiento);
			crearTicketMovimientoProvisional(sqlSession, movimiento);
			
			sqlSession.commit();
		}
		catch (CajasServiceException e) {
			sqlSession.rollback();
			throw e;
		}
		catch (Exception e) {
			sqlSession.rollback();
			String msg = "Se ha producido un error insertando movimiento de caja por concepto: " + codConcepto + " : " + e.getMessage();
			log.error("crearMovimientoManual() - " + msg, e);
			throw new CajasServiceException(I18N.getTexto("Error al insertar movimiento de caja"), e);
		}
		finally {
			sqlSession.close();
		}

		return movimiento;
	}

	private void crearTicketMovimientoProvisional(SqlSession sqlSession, CajaMovimientoBean movimiento) throws ContadorServiceException, MarshallUtilException {
		MovimientoProvisionalBean movimientoProvisional = new MovimientoProvisionalBean();
		movimientoProvisional.setUidActividad(movimiento.getUidActividad());
		movimientoProvisional.setUidDiarioCaja(movimiento.getUidDiarioCaja());
		movimientoProvisional.setLinea(movimiento.getLinea());
		movimientoProvisional.setCodAlm(sesion.getAplicacion().getCodAlmacen());
		movimientoProvisional.setCodCaja(sesion.getAplicacion().getCodCaja());
		movimientoProvisional.setFecha(movimiento.getFecha());
		movimientoProvisional.setUsuario(movimiento.getUsuario());
		movimientoProvisional.setCodConcepto(movimiento.getCodConceptoMovimiento());
		movimientoProvisional.setCargo(movimiento.getCargo());
		movimientoProvisional.setAbono(movimiento.getAbono());
		movimientoProvisional.setConcepto(movimiento.getConcepto());
		movimientoProvisional.setDocumento(movimiento.getDocumento());
		movimientoProvisional.setCodMedPag(movimiento.getCodMedioPago());
		byte[] xml = MarshallUtil.crearXML(movimientoProvisional);
		Long idTicket = contadoresService.obtenerValorContador(ID_CONTADOR_MOV_PROVISIONALES, sesion.getAplicacion().getUidActividad());
		
		TicketBean ticket = new TicketBean();
		ticket.setUidActividad(sesion.getAplicacion().getUidActividad());
		ticket.setUidTicket(UUID.randomUUID().toString());
		ticket.setCodAlmacen(sesion.getAplicacion().getCodAlmacen());
		ticket.setIdTicket(idTicket);
		ticket.setFecha(new Date());
		ticket.setTicket(xml);
		ticket.setProcesado(false);
		ticket.setCodcaja(sesion.getAplicacion().getCodCaja());
		ticket.setIdTipoDocumento(1000020L);
		ticket.setCodTicket("MOV-" + sesion.getAplicacion().getCodAlmacen() + idTicket);
		ticket.setSerieTicket(sesion.getAplicacion().getCodAlmacen());
		ticket.setFirma("*");
		ticket.setLocatorId(ticket.getUidTicket());
		
		ticketMapper.insert(ticket);
    }
	
	@Override
	public Caja crearCaja(Date fechaApertura) throws CajasServiceException, CajaEstadoException {
	    Caja caja = super.crearCaja(fechaApertura);
	    Dispositivos.abrirCajon();
	    return caja;
	}
	
}
