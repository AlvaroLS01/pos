/**
 * ComerZZia 3.0 Copyright (c) 2008-2015 Comerzzia, S.L. All Rights Reserved. THIS WORK IS SUBJECT TO SPAIN AND
 * INTERNATIONAL COPYRIGHT LAWS AND TREATIES. NO PART OF THIS WORK MAY BE USED, PRACTICED, PERFORMED COPIED,
 * DISTRIBUTED, REVISED, MODIFIED, TRANSLATED, ABRIDGED, CONDENSED, EXPANDED, COLLECTED, COMPILED, LINKED, RECAST,
 * TRANSFORMED OR ADAPTED WITHOUT THE PRIOR WRITTEN CONSENT OF COMERZZIA, S.L. ANY USE OR EXPLOITATION OF THIS WORK
 * WITHOUT AUTHORIZATION COULD SUBJECT THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY. CONSULT THE END USER LICENSE
 * AGREEMENT FOR INFORMATION ON ADDITIONAL RESTRICTIONS.
 */

package com.comerzzia.pos.services.cajas;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.core.util.fechas.Fecha;
import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.pos.persistence.cajas.CajaBean;
import com.comerzzia.pos.persistence.cajas.CajaExample;
import com.comerzzia.pos.persistence.cajas.CajaMapper;
import com.comerzzia.pos.persistence.cajas.conceptos.CajaConceptoBean;
import com.comerzzia.pos.persistence.cajas.movimientos.CajaMovimientoBean;
import com.comerzzia.pos.persistence.cajas.movimientos.CajaMovimientoExample;
import com.comerzzia.pos.persistence.cajas.movimientos.CajaMovimientoKey;
import com.comerzzia.pos.persistence.cajas.movimientos.CajaMovimientoMapper;
import com.comerzzia.pos.persistence.cajas.movimientos.DetalleResumenCajaDto;
import com.comerzzia.pos.persistence.cajas.recuentos.CajaLineaRecuentoBean;
import com.comerzzia.pos.persistence.cajas.recuentos.CajaLineaRecuentoExample;
import com.comerzzia.pos.persistence.cajas.recuentos.CajaLineaRecuentoMapper;
import com.comerzzia.pos.persistence.core.contadores.ContadorBean;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.persistence.mybatis.SpringTransactionSqlSession;
import com.comerzzia.pos.persistence.tickets.TicketBean;
import com.comerzzia.pos.services.cajas.conceptos.CajaConceptosServices;
import com.comerzzia.pos.services.core.contadores.ContadorNotFoundException;
import com.comerzzia.pos.services.core.contadores.ContadorServiceException;
import com.comerzzia.pos.services.core.contadores.ServicioContadores;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.core.documentos.Documentos;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.tiendas.Tienda;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.mediospagos.MediosPagosService;
import com.comerzzia.pos.services.ticket.TicketsService;
import com.comerzzia.pos.services.ticket.TicketsServiceException;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;
import com.comerzzia.pos.util.xml.MarshallUtil;
import com.comerzzia.pos.util.xml.MarshallUtilException;

@Component
public class CajasService {

	public static final String CODIGO_CONCEPTO_APERTURA = "00";

	protected static final Logger log = Logger.getLogger(CajasService.class);

	@Autowired
	protected Sesion sesion;

	@Autowired
	protected TicketsService ticketsService;

	@Autowired
	protected CajaConceptosServices cajaConceptosServices;
	
	@Autowired
	private VariablesServices variablesServices;

	@Autowired
	protected ServicioContadores servicioContadores;

	@Autowired
	protected CajaLineaRecuentoMapper cajaLineaRecuentoMapper;

	@Autowired
	protected CajaMovimientoMapper cajaMovimientoMapper;

	@Autowired
	protected CajaMapper cajaMapper;

	/**
	 * Obtiene el próximo id línea de recuento de caja para la caja actualmente abierta en sesión
	 *
	 * @param sqlSession
	 * @return Integer (idLinea)
	 * @throws CajasServiceException
	 */
	public Integer consultarProximaLineaRecuentoCaja(SqlSession sqlSession) throws CajasServiceException {
		String uidActividad = sesion.getAplicacion().getUidActividad();
		String uidDiarioCaja = sesion.getSesionCaja().getUidDiarioCaja();
		try {
			log.debug("consultarProximaLineaRecuentoCaja() - Obteniendo próximo número de línea de recuento de caja para uidDiarioCaja: " + uidDiarioCaja);

			CajaLineaRecuentoExample exampleCajaLineaRecuento = new CajaLineaRecuentoExample();
			exampleCajaLineaRecuento.or().andUidActividadEqualTo(uidActividad).andUidDiarioCajaEqualTo(uidDiarioCaja);
			Integer linea = cajaLineaRecuentoMapper.selectMaximaLineaRecuento(exampleCajaLineaRecuento);
			if (linea == null) {
				linea = 1;
			}
			return linea;
		}
		catch (Exception e) {
			sqlSession.rollback();
			String msg = "Se ha producido un error consultando siguiente línea de recuento de caja para uidDiarioCaja: " + uidDiarioCaja + " : " + e.getMessage();
			log.error("consultarProximaLineaRecuentoCaja() - " + msg, e);
			throw new CajasServiceException(I18N.getTexto("Error al consultar los recuentos de cajas en el sistema"), e);
		}
	}

	/**
	 * Obtiene el próximo id línea de detalle de caja para la caja actualmente abierta en sesión
	 *
	 * @param sqlSession
	 * @return Integer (idLinea)
	 * @throws CajasServiceException
	 */
	public Integer consultarProximaLineaDetalleCaja(SqlSession sqlSession) throws CajasServiceException {
		String uidActividad = sesion.getAplicacion().getUidActividad();
		String uidDiarioCaja = sesion.getSesionCaja().getUidDiarioCaja();
		try {
			log.debug("consultarProximaLineaDetalleCaja() - Obteniendo próximo número de línea de detalle de caja para uidDiarioCaja: " + uidDiarioCaja);

			CajaMovimientoExample exampleCajaMovimiento = new CajaMovimientoExample();
			exampleCajaMovimiento.or().andUidDiarioCajaEqualTo(uidDiarioCaja).andUidActividadEqualTo(uidActividad);
			log.debug("crearMovimiento() - obteniendo el numero de linea del movimiento");
			Integer linea = cajaMovimientoMapper.selectMaximaLineaMovimiento(exampleCajaMovimiento);
			if (linea == null) {
				linea = 1;
			}
			return linea;
		}
		catch (Exception e) {
			sqlSession.rollback();
			String msg = "Se ha producido un error consultando siguiente línea de detalle de caja para uidDiarioCaja: " + uidDiarioCaja + " : " + e.getMessage();
			log.error("consultarProximaLineaDetalleCaja() - " + msg, e);
			throw new CajasServiceException(I18N.getTexto("Error al consultar los movimientos de caja en el sistema"), e);
		}
	}

	/**
	 * Registra un nuevo movimiento de apertura de caja según los parámetros indicados
	 *
	 * @param importe
	 * @param fecha
	 * @throws CajasServiceException
	 */
	public void crearMovimientoApertura(BigDecimal importe, Date fecha) throws CajasServiceException {
		log.debug("crearMovimientoApertura() - Registrando movimiento de apertura de caja por importe: " + importe);
		SqlSession sqlSession = SpringContext.getBean(SpringTransactionSqlSession.class);
		try {
			sqlSession.openSession(SessionFactory.openSession());
			CajaMovimientoBean movimiento = new CajaMovimientoBean();
			movimiento.setCargo(importe);
			movimiento.setConcepto(I18N.getTexto("SALDO INICIAL"));
			movimiento.setCodMedioPago(MediosPagosService.medioPagoDefecto.getCodMedioPago());
			movimiento.setCodConceptoMovimiento(CODIGO_CONCEPTO_APERTURA);
			movimiento.setFecha(fecha);
			movimiento.setUsuario(sesion.getSesionUsuario().getUsuario().getUsuario());

			crearMovimiento(sqlSession, movimiento);
			sqlSession.commit();
		}
		catch (CajasServiceException e) {
			sqlSession.rollback();
			throw e;
		}
		catch (Exception e) {
			sqlSession.rollback();
			String msg = "Se ha producido un error insertando movimiento de caja por apertura de caja: " + e.getMessage();
			log.error("crearMovimientoApertura() - " + msg, e);
			throw new CajasServiceException(I18N.getTexto("Error al insertar movimiento de caja"), e);
		}
		finally {
			sqlSession.close();
		}
	}

	/**
	 * Registra un nuevo movimiento manual según los parámetros indicados
	 *
	 * @param importe
	 * @param codConcepto
	 * @param documento
	 * @throws CajasServiceException
	 */
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
			if(CajaConceptoBean.MOV_ENTRADA.equals(concepto.getInOut())) {
				movimiento.setCargo(importe);
			}else if(CajaConceptoBean.MOV_SALIDA.equals(concepto.getInOut())) {
				movimiento.setAbono(importe);
			}else {
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

	/**
	 * Registra un nuevo movimiento indicado por parámetro a la caja abierta actualmente.
	 *
	 * @param movimiento
	 *            :: Nuevo movimiento
	 * @throws CajasServiceException
	 */
	public void crearMovimiento(CajaMovimientoBean movimiento) throws CajasServiceException {
		SqlSession sqlSession = SpringContext.getBean(SpringTransactionSqlSession.class);
		try {
			sqlSession.openSession(SessionFactory.openSession());
			movimiento.setUsuario(sesion.getSesionUsuario().getUsuario().getUsuario());
			crearMovimiento(sqlSession, movimiento);
			sqlSession.commit();
		}
		catch (CajasServiceException e) {
			sqlSession.rollback();
			throw e;
		}
		catch (Exception e) {
			sqlSession.rollback();
			String msg = "Se ha producido un error insertando movimiento de caja con código de pago: " + movimiento.getCodMedioPago() + " : " + e.getMessage();
			log.error("crearMovimiento() - " + msg, e);
			throw new CajasServiceException(I18N.getTexto("Error al insertar movimiento de caja"), e);
		}
		finally {
			sqlSession.close();
		}
	}

	/**
	 * Registra un nuevo movimiento indicado por parámetro a la caja abierta actualmente.
	 *
	 * @param sqlSession
	 * @param movimiento
	 *            :: Nuevo movimiento
	 * @throws CajasServiceException
	 */
	public void crearMovimiento(SqlSession sqlSession, CajaMovimientoBean movimiento) throws CajasServiceException {
		try {
			String uidActividad = sesion.getAplicacion().getUidActividad();
			String uidDiarioCaja = sesion.getSesionCaja().getUidDiarioCaja();

			// añadimos al objeto el uidActividad y el uiddiarioCaja de sesion
			movimiento.setUidActividad(uidActividad);
			movimiento.setUidDiarioCaja(uidDiarioCaja);
			movimiento.setUsuario(sesion.getSesionUsuario().getUsuario().getUsuario());

			if (movimiento.getLinea() == null) {
				Integer idLinea = consultarProximaLineaDetalleCaja(sqlSession);
				movimiento.setLinea(idLinea);
			}
			log.debug("crearMovimiento() - Insertando movimiento de caja");
			cajaMovimientoMapper.insert(movimiento);
		}
		catch (Exception e) {
			String msg = "Se ha producido un error insertando movimiento de caja con código de pago: " + movimiento.getCodMedioPago() + " : " + e.getMessage();
			log.error("crearMovimiento() - " + msg, e);
			throw new CajasServiceException(I18N.getTexto("Error al insertar movimiento de caja"), e);
		}
	}

	public boolean eliminarMovimiento(CajaMovimientoKey cajaMovimiento) throws CajasServiceException {
		SqlSession sqlSession = new SqlSession();
		try {
			sqlSession.openSession(SessionFactory.openSession());

			log.debug("crearMovimiento() - Eliminando movimiento de caja " + cajaMovimiento);
			int delete = cajaMovimientoMapper.deleteByPrimaryKey(cajaMovimiento);

			return delete > 0;
		}
		catch (Exception e) {
			sqlSession.rollback();
			String msg = "Se ha producido un error eliminando movimiento de caja: " + cajaMovimiento + " : " + e.getMessage();
			log.error("crearMovimiento() - " + msg, e);
			throw new CajasServiceException(I18N.getTexto("Error al insertar movimiento de caja"), e);
		}
		finally {
			sqlSession.close();
		}
	}

	/**
	 * Elimina todas las líneas de recuento registradas de la caja y registra nuevamente las actuales.
	 *
	 * @param caja
	 * @throws CajasServiceException
	 */
	public void salvarRecuento(Caja caja) throws CajasServiceException {
		SqlSession sqlSession = SpringContext.getBean(SpringTransactionSqlSession.class);
		try {
			log.debug("salvarRecuento() - Salvando recuento para caja con uid: " + caja.getUidDiarioCaja());
			sqlSession.openSession(SessionFactory.openSession());

			String uidActividad = sesion.getAplicacion().getUidActividad();

			CajaLineaRecuentoExample example = new CajaLineaRecuentoExample();
			example.or().andUidActividadEqualTo(uidActividad).andUidDiarioCajaEqualTo(caja.getUidDiarioCaja());

			log.debug("salvarRecuento() - Borramos todas las líneas de recuentos anteriores...");
			cajaLineaRecuentoMapper.deleteByExample(example);

			log.debug("salvarRecuento() - Insertamos nuevas líneas de recuento...");
			Integer linea = 0;
			for (CajaLineaRecuentoBean lineaRecuento : caja.getLineasRecuento()) {
				lineaRecuento.setUidDiarioCaja(caja.getUidDiarioCaja());
				lineaRecuento.setUidActividad(uidActividad);
				lineaRecuento.setLinea(linea);
				cajaLineaRecuentoMapper.insert(lineaRecuento);
				linea++;
			}
			sqlSession.commit();
		}
		catch (Exception e) {
			sqlSession.rollback();
			String msg = "Se ha producido un error salvando recuento de caja: " + e.getMessage();
			log.error("crearLineaRecuento() - " + msg, e);
			throw new CajasServiceException(I18N.getTexto("Error al salvar recuento de caja"), e);
		}
		finally {
			sqlSession.close();
		}
	}

	/**
	 * Crea un nuevo registro en la tabla de cajas cabecera indicando la fecha apertura pasada por parámetro. Utiliza la
	 * tienda y caja de la sesión. Si ya existe una caja abierta para esa caja y tienda lanza una excepción.
	 *
	 * @param fechaApertura
	 *            :: Fecha con la que se abrirá la caja
	 * @return Caja
	 * @throws CajasServiceException
	 * @throws CajaEstadoException
	 *             :: Lanzada si la caja ya está abierta
	 */
	public Caja crearCaja(Date fechaApertura) throws CajasServiceException, CajaEstadoException {
		SqlSession sqlSession = SpringContext.getBean(SpringTransactionSqlSession.class);
		try {
			sqlSession.openSession(SessionFactory.openSession());
			String uidActividad = sesion.getAplicacion().getUidActividad();
			String codAlmacen = sesion.getAplicacion().getCodAlmacen();
			String codCaja = sesion.getAplicacion().getCodCaja();

			CajaExample exampleCaja = new CajaExample();
			exampleCaja.or().andUidActividadEqualTo(uidActividad).andCodAlmacenEqualTo(codAlmacen).andCodcajaEqualTo(codCaja).andFechaCierreIsNull();
			log.debug("crearCaja() - consultado caja con codAlmacen" + codAlmacen + "  y codCaja " + codCaja);
			List<CajaBean> cajasBean = cajaMapper.selectByExample(exampleCaja);

			if (!cajasBean.isEmpty()) {
				log.warn("crearCaja() - Error creando caja. La caja esta marcada como abierta");
				throw new CajaEstadoException(I18N.getTexto("Ya existe una caja abierta en el sistema "));
			}
			CajaBean cajaBean = new CajaBean();
			cajaBean.setUidActividad(uidActividad);
			cajaBean.setUidDiarioCaja(UUID.randomUUID().toString());
			cajaBean.setCodAlmacen(codAlmacen);
			cajaBean.setCodCaja(codCaja);
			cajaBean.setUsuario(sesion.getSesionUsuario().getUsuario().getUsuario());
			cajaBean.setFechaApertura(fechaApertura);
			cajaBean.setFechaCierre(null);
			cajaBean.setUsuarioCierre(null);
			cajaBean.setFechaEnvio(null);
			
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			Date fechaContable = formatter.parse(formatter.format(fechaApertura));

			
			cajaBean.setFechaContable(fechaContable);
			log.debug("crearCaja() - Insertando caja con codAlmacen " + codAlmacen + "  y codCaja " + codCaja);

			// Dependiendo si la fecha tiene hora o no, llamamos a un método u otro del mapper
			Calendar calendarApertura = Calendar.getInstance();
			calendarApertura.setTime(fechaApertura);
			if (calendarApertura.get(Calendar.HOUR_OF_DAY) == 0 && calendarApertura.get(Calendar.MINUTE) == 0 && calendarApertura.get(Calendar.SECOND) == 0
			        && calendarApertura.get(Calendar.MILLISECOND) == 0) {
				cajaMapper.insertFechaAperturaDate(cajaBean);
			}
			else {
				cajaMapper.insertFechaAperturaDateTime(cajaBean);
			}

			sqlSession.commit();
			return new Caja(cajaBean);
		}
		catch (CajaEstadoException e) {
			sqlSession.rollback();
			String msg = "Se ha producido un error insertando caja con fecha de apertura" + fechaApertura + " :" + e.getMessage();
			log.error("crearCaja() - " + msg);
			throw e;
		}
		catch (Exception e) {
			sqlSession.rollback();
			String msg = "Se ha producido un error insertando caja con fecha de apertura" + fechaApertura + " :" + e.getMessage();
			log.error("crearCaja() - " + msg, e);
			throw new CajasServiceException(I18N.getTexto("Error realizando apertura de caja"), e);
		}
		finally {
			sqlSession.close();
		}
	}

	public void cerrarCaja(SqlSession sqlSession, Caja caja, Date fechaCierre) throws CajasServiceException, DocumentoException, ContadorNotFoundException, ContadorServiceException,
	        MarshallUtilException, TicketsServiceException, UnsupportedEncodingException {
		Tienda tienda = sesion.getAplicacion().getTienda();
		String codTipoDocumento = sesion.getAplicacion().getDocumentos().getDocumento(Documentos.CIERRE_CAJA).getCodtipodocumento();

		log.debug("cerrarCaja() - Obteniendo contado para identificador...");
		Map<String, String> parametrosContador = new HashMap<>();
		parametrosContador.put("CODEMP", tienda.getAlmacenBean().getCodEmpresa());
		parametrosContador.put("CODALM", tienda.getAlmacenBean().getCodAlmacen());
		parametrosContador.put("CODSERIE", tienda.getAlmacenBean().getCodAlmacen());
		parametrosContador.put("CODCAJA", caja.getCodCaja());
		parametrosContador.put("CODTIPODOCUMENTO", codTipoDocumento);
		parametrosContador.put("PERIODO", ((new Fecha(caja.getFechaApertura())).getAño().toString()));

		TipoDocumentoBean documentoActivo = sesion.getAplicacion().getDocumentos().getDocumento(codTipoDocumento);

		ContadorBean ticketContador = servicioContadores.obtenerContador(documentoActivo.getIdContador(), parametrosContador, sesion.getAplicacion().getUidActividad());
		
		String codTicket = servicioContadores.obtenerValorTotalConSeparador(ticketContador.getDivisor3(), ticketContador.getValorFormateado());
		
		caja.setFechaEnvio(new Date());
		caja.setUsuarioCierre(sesion.getSesionUsuario().getUsuario().getUsuario());
		TicketBean ticket = new TicketBean();
		ticket.setCodAlmacen(tienda.getAlmacenBean().getCodAlmacen());
		ticket.setCodcaja(caja.getCodCaja());
		ticket.setFecha(caja.getFechaApertura());
		ticket.setIdTicket(ticketContador.getValor());
		ticket.setUidTicket(caja.getUidDiarioCaja());
		ticket.setTicket(MarshallUtil.crearXML(caja));
		log.debug("TICKET: " + ticket.getUidTicket() + "\n" + new String(ticket.getTicket(), "UTF-8") + "\n");
		ticket.setIdTipoDocumento(documentoActivo.getIdTipoDocumento());
		ticket.setSerieTicket(ticketContador.getDivisor3());
		ticket.setCodTicket(codTicket);
		ticket.setFirma("*");
		ticket.setLocatorId(ticket.getUidTicket());

		ticketsService.insertarTicket(sqlSession, ticket, false);

		CajaBean cajaBean = new CajaBean();
		cajaBean.setUidDiarioCaja(caja.getUidDiarioCaja());
		cajaBean.setUidActividad(sesion.getAplicacion().getUidActividad());
		cajaBean.setFechaCierre(fechaCierre);
		cajaBean.setUsuarioCierre(sesion.getSesionUsuario().getUsuario().getUsuario());

		// Dependiendo si la fecha tiene hora o no, llamamos a un método u otro del mapper
		Calendar calendarCierre = Calendar.getInstance();
		calendarCierre.setTime(fechaCierre);
		if (calendarCierre.get(Calendar.HOUR_OF_DAY) == 0 && calendarCierre.get(Calendar.MINUTE) == 0 && calendarCierre.get(Calendar.SECOND) == 0 && calendarCierre.get(Calendar.MILLISECOND) == 0) {
			cajaMapper.cierreCajaDateByPrimaryKey(cajaBean);
		}
		else {
			cajaMapper.cierreCajaDateTimeByPrimaryKey(cajaBean);
		}
	}

	/**
	 * Cierra la caja indicada con la fecha que se pasa por parámetro.
	 *
	 * @param caja
	 *            Caja abierta
	 * @param fechaCierre
	 *            Fecha de cierre de la caja
	 * @throws CajasServiceException
	 */
	public void cerrarCaja(Caja caja, Date fechaCierre) throws CajasServiceException {
		SqlSession sqlSession = SpringContext.getBean(SpringTransactionSqlSession.class);
		try {
			log.debug("cerrarCaja() - Cerrando caja con fecha: " + fechaCierre);
			sqlSession.openSession(SessionFactory.openSession());

			cerrarCaja(sqlSession, caja, fechaCierre);

			sqlSession.commit();
		}
		catch (Exception e) {
			sqlSession.rollback();
			String msg = "Se ha producido un error cerrando caja con fecha de cierre" + fechaCierre + " :" + e.getMessage();
			log.error("cerrarCaja() - " + msg, e);
			throw new CajasServiceException(I18N.getTexto("Error realizando cierre de caja"), e);
		}
		finally {
			sqlSession.close();
		}
	}

	public Caja consultarUltimaCajaCerrada() throws CajasServiceException, CajaEstadoException {
		SqlSession sqlSession = new SqlSession();
		try {
			sqlSession.openSession(SessionFactory.openSession());
			CajaExample exampleCaja = new CajaExample();
			exampleCaja.or().andUidActividadEqualTo(sesion.getAplicacion().getUidActividad()).andCodAlmacenEqualTo(sesion.getAplicacion().getCodAlmacen())
			        .andCodcajaEqualTo(sesion.getAplicacion().getCodCaja()).andFechaCierreIsNotNull();
			exampleCaja.setOrderByClause(exampleCaja.ORDER_BY_FECHA_APERTURA_DESC);
			log.debug("consultarUltimaCajaCerrada() - Consultado caja cerrada en sesion");
			List<CajaBean> cajasBean = cajaMapper.selectByExample(exampleCaja);

			if (cajasBean.isEmpty()) {
				throw new CajaEstadoException(I18N.getTexto("No existen cajas cerrada en el sistema"));
			}
			return new Caja(cajasBean.get(0));
		}
		catch (CajaEstadoException e) {
			throw e;
		}
		catch (Exception e) {
			String msg = "Se ha producido un error consultando caja cerrada en sesion :" + e.getMessage();
			log.error("consultarCajaAbierta() - " + msg, e);
			throw new CajasServiceException(I18N.getTexto("Error al consultar caja abierta en sesión del sistema"), e);
		}
		finally {
			sqlSession.close();
		}
	}

	/**
	 * Devuelve la caja abierta actualmente para la tienda y caja de la sesión. Si no existe ninguna caja abierta lanza
	 * una excepción.
	 *
	 * @return Caja
	 * @throws CajasServiceException
	 * @throws CajaEstadoException
	 *             :: Lanzada si no existe caja abierta
	 */
	public Caja consultarCajaAbierta() throws CajasServiceException, CajaEstadoException {
		SqlSession sqlSession = new SqlSession();
		try {
			sqlSession.openSession(SessionFactory.openSession());
			CajaExample exampleCaja = new CajaExample();
			exampleCaja.or().andUidActividadEqualTo(sesion.getAplicacion().getUidActividad()).andCodAlmacenEqualTo(sesion.getAplicacion().getCodAlmacen())
			        .andCodcajaEqualTo(sesion.getAplicacion().getCodCaja()).andFechaCierreIsNull();
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
			log.error("consultarCajaAbierta() - " + msg, e);
			throw new CajasServiceException(I18N.getTexto("Error al consultar caja abierta en sesión del sistema"), e);
		}
		finally {
			sqlSession.close();
		}
	}

	/**
	 * Actualiza la caja pasada por parámetro con los movimientos registrados en BBDD para ella distinguiendo entre
	 * movimientos de venta y movimientos manuales
	 *
	 * @param caja
	 *            CajaBean
	 * @throws CajasServiceException
	 */
	public void consultarMovimientos(Caja caja) throws CajasServiceException {
		SqlSession sqlSession = new SqlSession();
		try {
			log.debug("consultarMovimientos() - Consultando movimientos de caja para " + caja.getUidDiarioCaja());
			sqlSession.openSession(SessionFactory.openSession());
			CajaMovimientoExample exampleCajaMovimiento = new CajaMovimientoExample();
			exampleCajaMovimiento.or().andUidActividadEqualTo(sesion.getAplicacion().getUidActividad()).andUidDiarioCajaEqualTo(caja.getUidDiarioCaja()).andIdDocumentoIsNotNull();
			exampleCajaMovimiento.setOrderByClause(CajaMovimientoExample.ORDER_BY_LINEA);
			List<CajaMovimientoBean> movimientosVentas = cajaMovimientoMapper.selectByExample(exampleCajaMovimiento);

			exampleCajaMovimiento.clear();
			exampleCajaMovimiento.or().andUidActividadEqualTo(sesion.getAplicacion().getUidActividad()).andUidDiarioCajaEqualTo(caja.getUidDiarioCaja()).andIdDocumentoIsNull();
			exampleCajaMovimiento.setOrderByClause(CajaMovimientoExample.ORDER_BY_LINEA);
			List<CajaMovimientoBean> movimientosApuntes = cajaMovimientoMapper.selectByExample(exampleCajaMovimiento);

			caja.setMovimientosVenta(movimientosVentas);
			caja.setMovimientosApuntes(movimientosApuntes);
		}
		catch (Exception e) {
			String msg = "Se ha producido un error consultando los movimientos de caja con uid: " + caja.getUidDiarioCaja() + " : " + e.getMessage();
			log.error("consultarMovimientos() - " + msg);
			throw new CajasServiceException(I18N.getTexto("Error al consultar los movimientos de caja en el sistema"), e);
		}
		finally {
			sqlSession.close();
		}
	}

	/**
	 * Actualiza la caja pasada por parámetro con las líneas de recuento registradas en BBDD para ella
	 *
	 * @param caja
	 * @throws CajasServiceException
	 */
	public void consultarRecuento(Caja caja) throws CajasServiceException {
		SqlSession sqlSession = new SqlSession();

		try {
			sqlSession.openSession(SessionFactory.openSession());
			CajaLineaRecuentoExample exampleCajaLineaRecuento = new CajaLineaRecuentoExample();

			exampleCajaLineaRecuento.or().andUidDiarioCajaEqualTo(caja.getUidDiarioCaja());
			log.debug("consultarRecuento() - Consultando recuento para caja con uid: " + caja.getUidDiarioCaja());
			List<CajaLineaRecuentoBean> recuento = cajaLineaRecuentoMapper.selectByExample(exampleCajaLineaRecuento);
			caja.setLineasRecuento(recuento);
		}
		catch (Exception e) {
			String msg = "Se ha producido un error consultando los recuentos de caja con UID: " + caja.getUidDiarioCaja() + " : " + e.getMessage();
			log.error("consultarRecuento() - " + msg);
			throw new CajasServiceException(I18N.getTexto("Error al consultar los recuentos de cajas en el sistema"), e);
		}
		finally {
			sqlSession.close();
		}
	}

	public CajaMovimientoBean consultarMovimientoApartado(String uidDiarioCaja, int linea, String uidActividad) throws CajasServiceException {
		SqlSession sqlSession = new SqlSession();

		try {
			sqlSession.openSession(SessionFactory.openSession());
			CajaMovimientoKey key = new CajaMovimientoKey();
			key.setLinea(linea);
			key.setUidActividad(uidActividad);
			key.setUidDiarioCaja(uidDiarioCaja);

			return cajaMovimientoMapper.selectByPrimaryKey(key);
		}
		catch (Exception e) {
			String msg = "Se ha producido un error consultando los recuentos de caja con UID: " + uidDiarioCaja + " : " + e.getMessage();
			log.error("consultarRecuento() - " + msg);
			throw new CajasServiceException(I18N.getTexto("Error al consultar los recuentos de cajas en el sistema."), e);
		}
		finally {
			sqlSession.close();
		}
	}
	

	public boolean validarImporteBloqueoRetirada() throws CajasServiceException{
		log.debug("validarImporteBloqueoRetirada()");
		boolean isBloqueo = false;
		BigDecimal cantidadBloqueo = variablesServices.getVariableAsBigDecimal(VariablesServices.IMPORTE_BLOQUEO_RETIRADA);
		if(cantidadBloqueo != null && !BigDecimalUtil.isIgualACero(cantidadBloqueo)){
			BigDecimal acumulado = consultarAcumuladoCaja(sesion.getAplicacion().getUidActividad(), sesion.getSesionCaja().getUidDiarioCaja());
			log.debug("validarImporteBloqueoRetirada() Acumulado: " + acumulado + " Bloqueo fijado en: " + cantidadBloqueo);
			if(BigDecimalUtil.isMayor(acumulado, cantidadBloqueo)){
				isBloqueo = true;
			}
		}
		return isBloqueo;
	}
	
	public boolean validarImporteAvisoRetirada() throws CajasServiceException{
		log.debug("validarImporteAvisoRetirada()");
		boolean isRetirada = false;
		BigDecimal avisoRetirada = variablesServices.getVariableAsBigDecimal(VariablesServices.IMPORTE_AVISO_RETIRADA);
		if(avisoRetirada != null && !BigDecimalUtil.isIgualACero(avisoRetirada)){
			BigDecimal acumulado = consultarAcumuladoCaja(sesion.getAplicacion().getUidActividad(), sesion.getSesionCaja().getUidDiarioCaja());
			log.debug("validarImporteAvisoRetirada() Acumulado: " + acumulado + " Aviso fijado en: " + avisoRetirada);
			if(BigDecimalUtil.isMayor(acumulado, avisoRetirada)){				
				isRetirada = true;
			}
		}
		return isRetirada;
	}
	
	public BigDecimal consultarAcumuladoCaja(String uidActividad, String uidDiarioCaja) throws CajasServiceException {
		log.debug("consultarAcumuladoCaja()");
		SqlSession sqlSession = new SqlSession();
		BigDecimal acumuladorCaja = null;

		try {
			sqlSession.openSession(SessionFactory.openSession());
			Map<String, Object> params = new HashMap<String, Object>(); 
			params.put("uidActividad", uidActividad);
			params.put("uidDiarioCaja", uidDiarioCaja);		
			BigDecimal contAcumulado = cajaMapper.contAcumuladoCaja(params);
			if(contAcumulado == null){
				acumuladorCaja = BigDecimal.ZERO;
			}else{
				acumuladorCaja = contAcumulado;
			}
			return acumuladorCaja;
		}
		catch (Exception e) {
			String msg = "Se ha producido un error consultando la cantidad acumulada en caja: " + uidDiarioCaja + " : " + e.getMessage();
			log.error("consultarAcumuladoCaja() - " + msg);
			throw new CajasServiceException(I18N.getTexto("Error al consultar la cantidada acumulada en caja."), e);
		}
		finally {
			sqlSession.close();
		}
	}

	@SuppressWarnings("deprecation")
    public void consultarTotales(Caja caja) throws CajasServiceException {
		SqlSession sqlSession = new SqlSession();
		try {
			log.debug("consultarTotales() - Consultando totales de caja para " + caja.getUidDiarioCaja());
			sqlSession.openSession(SessionFactory.openSession());
			
			List<DetalleResumenCajaDto> resumenCaja = cajaMovimientoMapper.selectResumenCaja(sesion.getAplicacion().getUidActividad(), caja.getUidDiarioCaja());
			
			Totales totales = new Totales();
			for(DetalleResumenCajaDto resumen : resumenCaja) {
				if(resumen.getTipoDocumento().equals("VENTAS")) {
					totales.setTotalVentasEntrada(resumen.getImporteTotal() != null ? resumen.getImporteTotal().abs() : BigDecimal.ZERO);
					caja.setNumTicketsEntrada(resumen.getNumDocumentos().intValue());
				}
				else if(resumen.getTipoDocumento().equals("DEVOLUCIONES")) {
					totales.setTotalVentasSalida(resumen.getImporteTotal() != null ? resumen.getImporteTotal().abs() : BigDecimal.ZERO);
					caja.setNumTicketsSalida(resumen.getNumDocumentos().intValue());
				}
				else if(resumen.getTipoDocumento().equals("MOV_ENTRADA")) {
					totales.setTotalApuntesEntrada(resumen.getImporteTotal() != null ? resumen.getImporteTotal().abs() : BigDecimal.ZERO);
				}
				else if(resumen.getTipoDocumento().equals("MOV_SALIDA")) {
					totales.setTotalApuntesSalida(resumen.getImporteTotal() != null ? resumen.getImporteTotal().abs() : BigDecimal.ZERO);				
				}
			}
			caja.setTotales(totales);
		}
		catch (Exception e) {
			String msg = "Se ha producido un error consultando los totales de caja con uid: " + caja.getUidDiarioCaja() + " : " + e.getMessage();
			log.error("consultarTotales() - " + msg, e);
			throw new CajasServiceException(I18N.getTexto("Error al consultar los totales de caja en el sistema"), e);
		}
		finally {
			sqlSession.close();
		}
    }

}
