package com.comerzzia.bimbaylola.pos.services.cajas;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.ByL.backoffice.rest.client.empresa.ByLEmpresasRest;
import com.comerzzia.bimbaylola.pos.persistence.cajas.recuentos.RecuentoBean;
import com.comerzzia.bimbaylola.pos.persistence.cajas.recuentos.RecuentoCajaBean;
import com.comerzzia.bimbaylola.pos.persistence.movimientostarjeta.CajaMovimientoTarjeta;
import com.comerzzia.bimbaylola.pos.persistence.ventas.cajas.apertura.AperturaCajaBean;
import com.comerzzia.bimbaylola.pos.persistence.ventas.cajas.apertura.AperturaCajaCabecera;
import com.comerzzia.bimbaylola.pos.persistence.ventas.cajas.cierre.CierreCaja;
import com.comerzzia.bimbaylola.pos.persistence.ventas.cajas.movimientos.MovimientosCajaBean;
import com.comerzzia.bimbaylola.pos.persistence.ventas.cajas.movimientos.salidabanco.MovimientosCajaSalidaBancoBean;
import com.comerzzia.bimbaylola.pos.persistence.ventas.tipooperacion.TipoOperacionVenta;
import com.comerzzia.bimbaylola.pos.services.apartados.ByLApartadosService;
import com.comerzzia.bimbaylola.pos.services.core.documentos.ByLDocumentos;
import com.comerzzia.bimbaylola.pos.services.core.documentos.propiedades.ByLPropiedadesDocumentoService;
import com.comerzzia.bimbaylola.pos.services.core.sesion.ByLSesionAplicacion;
import com.comerzzia.bimbaylola.pos.services.core.sesion.ByLSesionCaja;
import com.comerzzia.bimbaylola.pos.services.movimientos.CajaMovimientoTarjetaService;
import com.comerzzia.bimbaylola.pos.services.movimientos.exception.CajaMovimientoTarjetaException;
import com.comerzzia.bimbaylola.pos.services.reservas.pagogift.ReservasPagoGiftCardService;
import com.comerzzia.bimbaylola.pos.services.ticket.ByLTicketsService;
import com.comerzzia.bimbaylola.pos.services.ticket.aparcados.reservas.DatosFidelizadoReservaTicket;
import com.comerzzia.bimbaylola.pos.services.ticket.aparcados.reservas.PagoReservaTicket;
import com.comerzzia.bimbaylola.pos.services.ticket.aparcados.reservas.TicketReserva;
import com.comerzzia.bimbaylola.pos.services.ticket.aparcados.reservas.anticipos.MensajeAnticipoReservaBean;
import com.comerzzia.bimbaylola.pos.services.ticket.aparcados.reservas.anticipos.MensajeAnticipoReservaCabeceraBean;
import com.comerzzia.bimbaylola.pos.services.ventas.tipooperacion.TipoOperacionVentaService;
import com.comerzzia.core.util.fechas.Fecha;
import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.pos.persistence.cajas.CajaBean;
import com.comerzzia.pos.persistence.cajas.CajaExample;
import com.comerzzia.pos.persistence.cajas.movimientos.CajaMovimientoBean;
import com.comerzzia.pos.persistence.cajas.movimientos.CajaMovimientoExample;
import com.comerzzia.pos.persistence.cajas.recuentos.CajaLineaRecuentoBean;
import com.comerzzia.pos.persistence.core.contadores.ContadorBean;
import com.comerzzia.pos.persistence.core.documentos.propiedades.PropiedadDocumentoBean;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.persistence.mediosPagos.MedioPagoBean;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.persistence.mybatis.SpringTransactionSqlSession;
import com.comerzzia.pos.persistence.paises.PaisBean;
import com.comerzzia.pos.persistence.tickets.TicketBean;
import com.comerzzia.pos.services.cajas.Caja;
import com.comerzzia.pos.services.cajas.CajaEstadoException;
import com.comerzzia.pos.services.cajas.CajasService;
import com.comerzzia.pos.services.cajas.CajasServiceException;
import com.comerzzia.pos.services.core.contadores.ContadorNotFoundException;
import com.comerzzia.pos.services.core.contadores.ContadorServiceException;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.core.paises.PaisNotFoundException;
import com.comerzzia.pos.services.core.paises.PaisService;
import com.comerzzia.pos.services.core.paises.PaisServiceException;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.tiendas.Tienda;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.mediospagos.MediosPagosService;
import com.comerzzia.pos.services.ticket.TicketsServiceException;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;
import com.comerzzia.pos.util.xml.MarshallUtil;
import com.comerzzia.pos.util.xml.MarshallUtilException;
import com.comerzzia.rest.client.exceptions.RestException;
import com.comerzzia.rest.client.exceptions.RestHttpException;

@Component
@Primary
public class ByLCajasService extends CajasService {

	protected static final Logger log = Logger.getLogger(ByLCajasService.class);

	protected static final long ID_TIPO_DOCUMENTO_TR = 10004l;
	protected static final String COD_CONCEPTO_MOV_APERTURA_CAJA = "00";
	protected static final String COD_CONCEPTO_MOV_CAJA_SALIDA_BANCO = "20";
	protected static final String COD_DOCUMENTO_RECUENTO_CIERRE_CAJA = "CR";

	public static final String SIMPLIFIED = "SIMPLIFIED";
	public static final String REGULAR = "REGULAR";
	
	private static final String TAX_IN_SALE = "Tax in Sale";
	
	public static final String POSITIVE = "POSITIVE";
	public static final String NEGATIVE = "NEGATIVE";
	
	public static final String TIPOS_DOCUMENTOS_ORIGEN = "TIPOS_DOCUMENTOS_ORIGEN";
	
	@Autowired
	protected Sesion sesion;
	
	@Autowired
	protected VariablesServices variableService;
	
	@Autowired
	protected ByLTicketsService ticketsService;
	
	@Autowired
	protected PaisService paisService;
	
	@Autowired
	protected MediosPagosService mediosPagosService;
	
	@Autowired
	protected ReservasPagoGiftCardService pagoGiftCardService;
	
	@Autowired
	protected CajaMovimientoTarjetaService cajaMovimientoTarjetaService;
	
	@Autowired
	protected ByLPropiedadesDocumentoService propiedadesDocumentoService;
	
	@Autowired
	protected TipoOperacionVentaService tipoOperacionVentaService;
	
    @Autowired
    protected ByLDocumentos documentos;
	
	protected String idVariableGestionCaja = "GESTION.CAJA";

	public Caja consultarCajaAbierta() throws CajasServiceException, CajaEstadoException {

		SqlSession sqlSession = new SqlSession();

		try {

			sqlSession.openSession(SessionFactory.openSession());
			CajaExample exampleCaja = new CajaExample();

			/*
			 * Comprobamos si el tipo de variable de "Gestión Caja" es de tipo : N : Individual por cada terminal. S :
			 * Caja compartida con todos los terminales.
			 */
			if (variableService.getVariableAsString(idVariableGestionCaja).equals("N")) {
				exampleCaja.or().andUidActividadEqualTo(sesion.getAplicacion().getUidActividad()).andCodAlmacenEqualTo(sesion.getAplicacion().getCodAlmacen())
				        .andCodcajaEqualTo(sesion.getAplicacion().getCodCaja()).andFechaCierreIsNull();
			}
			else {
				exampleCaja.or().andUidActividadEqualTo(sesion.getAplicacion().getUidActividad()).andCodAlmacenEqualTo(sesion.getAplicacion().getCodAlmacen()).andFechaCierreIsNull();
			}

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
	 * Quita de sesión la caja que este abierta.
	 */
	public void quitarCajaSesion() {
		/* Si se ha cerrado la caja, la sacamos de sesión. */
		sesion.getSesionCaja().cerrarCaja();
	}

	/**
	 * Mete en sesión la caja que le pasemos.
	 * 
	 * @param caja
	 *            : Objeto tipo Caja.
	 */
	public void meterCajaSesion(Caja caja) {
		((ByLSesionCaja) sesion.getSesionCaja()).setCajaAbierta(caja);
	}

	/**
	 * Realiza la comprobación de que la caja "Master" estña abierta para poder realizar operaciones.
	 * 
	 * @return
	 */
	public Boolean comprobarCajaMaster() {

		Boolean abierta = true;

		try {
			consultarCajaAbierta();
		}
		catch (CajaEstadoException | CajasServiceException e) {
			abierta = false;
		}

		return abierta;

	}

	@SuppressWarnings("static-access")
	public Caja consultarUltimaCajaCerrada() throws CajasServiceException, CajaEstadoException {

		SqlSession sqlSession = new SqlSession();

		log.debug("consultarUltimaCajaCerrada() - Consultado caja cerrada en sesion");

		try {

			sqlSession.openSession(SessionFactory.openSession());

			CajaExample exampleCaja = new CajaExample();

			/*
			 * Comprobamos si el tipo de variable de "Gestión Caja" es de tipo : N : Individual por cada terminal. S :
			 * Caja compartida con todos los terminales.
			 */
			if (variableService.getVariableAsString(idVariableGestionCaja).equals("N")) {
				exampleCaja.or().andUidActividadEqualTo(sesion.getAplicacion().getUidActividad()).andCodAlmacenEqualTo(sesion.getAplicacion().getCodAlmacen())
				        .andCodcajaEqualTo(sesion.getAplicacion().getCodCaja()).andFechaCierreIsNotNull();;
			}
			else {
				exampleCaja.or().andUidActividadEqualTo(sesion.getAplicacion().getUidActividad()).andCodAlmacenEqualTo(sesion.getAplicacion().getCodAlmacen()).andFechaCierreIsNotNull();
			}

			exampleCaja.setOrderByClause(exampleCaja.ORDER_BY_FECHA_APERTURA_DESC);

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

	@Override
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

			for (CajaMovimientoBean movimiento : movimientosVentas) {
				if (movimiento.getIdTipoDocumento() == ID_TIPO_DOCUMENTO_TR) {
					if (BigDecimalUtil.isMayorACero(movimiento.getAbono())) {
						movimiento.setCargo(movimiento.getAbono().negate());
						movimiento.setAbono(new BigDecimal(0));
					}
				}
			}

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
	public void crearDocumentoAperturaCaja() throws Exception {
		log.debug("crearDocumentoAperturaCaja()");
		SqlSession sqlSession = SpringContext.getBean(SpringTransactionSqlSession.class);

		Caja cajaAbierta = sesion.getSesionCaja().getCajaAbierta();
		try {
			AperturaCajaBean aperturaCaja = new AperturaCajaBean();

			AperturaCajaCabecera aperturaCajaCabecera = new AperturaCajaCabecera();
			aperturaCaja.setCabecera(aperturaCajaCabecera);
			if (cajaAbierta != null) {
				log.debug("crearDocumentoAperturaCaja - Creando documento de apertura de caja para : " + cajaAbierta.getUidDiarioCaja());
				aperturaCajaCabecera.setUidActividad(sesion.getAplicacion().getUidActividad());
				aperturaCajaCabecera.setUidDiarioCaja(cajaAbierta.getUidDiarioCaja());
				aperturaCajaCabecera.setUidTicket(UUID.randomUUID().toString());
				aperturaCajaCabecera.setCodAlmacen(cajaAbierta.getCodAlm());
				aperturaCajaCabecera.setCodCaja(cajaAbierta.getCodCaja());
				aperturaCajaCabecera.setFechaApertura(cajaAbierta.getFechaApertura());
				aperturaCajaCabecera.setUsuario(cajaAbierta.getUsuario());

				Integer totalTurnos =  countTurnosCaja(cajaAbierta);
				aperturaCajaCabecera.setTurno(totalTurnos);

				aperturaCajaCabecera.setImporteApertura(cajaAbierta.getTotalEntradas());

				try {
					String moneda = null;
					PaisBean pais = paisService.consultarCodPais(sesion.getAplicacion().getTienda().getCliente().getCodpais());
					if (pais != null && StringUtils.isNotBlank(pais.getCodDivisa())) {
						moneda = pais.getCodDivisa();
					}
					aperturaCajaCabecera.setMoneda(moneda);
				}
				catch (PaisNotFoundException | PaisServiceException e) {
					log.error("crearDocumentoAperturaCaja() - No se ha podido consultar la moneda del país durante la creación del documento de apertura de caja : " + e.getMessage(), e);
				}

				if (aperturaCaja != null && aperturaCaja.getCabecera() != null) {

					Map<String, String> parametrosContador = new HashMap<>();

					parametrosContador.put("CODEMP", sesion.getAplicacion().getEmpresa().getCodEmpresa());
					parametrosContador.put("CODALM", sesion.getAplicacion().getTienda().getAlmacenBean().getCodAlmacen());
					parametrosContador.put("CODSERIE", sesion.getAplicacion().getTienda().getAlmacenBean().getCodAlmacen());
					parametrosContador.put("CODCAJA", sesion.getAplicacion().getCodCaja());
					parametrosContador.put("CODTIPODOCUMENTO", ByLDocumentos.APERTURA_CAJA_ERP);
					parametrosContador.put("PERIODO", ((new Fecha()).getAño().toString()));

					TipoDocumentoBean documento = sesion.getAplicacion().getDocumentos().getDocumento(ByLDocumentos.APERTURA_CAJA_ERP);
					ContadorBean ticketContador = null;
					if (documento != null) {
						ticketContador = servicioContadores.obtenerContador(documento.getIdContador(), parametrosContador, aperturaCaja.getCabecera().getUidActividad());
					}
					String codTicket = servicioContadores.obtenerValorTotalConSeparador(ticketContador.getDivisor3(), ticketContador.getValorFormateado());

					if (ticketContador != null) {
						TicketBean ticketBean = new TicketBean();
						ticketBean.setUidTicket(aperturaCaja.getCabecera().getUidTicket());
						ticketBean.setCodAlmacen(aperturaCaja.getCabecera().getCodAlmacen());
						ticketBean.setIdTicket(ticketContador.getValor());
						ticketBean.setFecha(new Date());
						
						aperturaCajaCabecera.setCodTicket(codTicket);
						aperturaCajaCabecera.setCodEmp(sesion.getAplicacion().getEmpresa().getCodEmpresa());

						byte[] xmlAperturaCaja = MarshallUtil.crearXML(aperturaCaja);
						ticketBean.setTicket(xmlAperturaCaja);

						ticketBean.setCodcaja(aperturaCaja.getCabecera().getCodCaja());
						ticketBean.setIdTipoDocumento(documento.getIdTipoDocumento());
						ticketBean.setCodTicket(codTicket);
						ticketBean.setFirma("*");
						ticketBean.setSerieTicket(ticketContador.getConfigContador().getValorDivisor3());
						
						
						ticketsService.insertarTicket(sqlSession, ticketBean, false);
						log.debug("crearDocumentoAperturaCaja - Documento de apertura de caja : " + cajaAbierta.getUidDiarioCaja() + " creado satisfactoriamente");
					}

				}
			}
		}
		catch (Exception e) {
			log.error("crearDocumentoAperturaCaja() - No se ha podido crear el documento de apertura para : " + cajaAbierta.getUidDiarioCaja());
		}
	}
	
	@Override
	public void cerrarCaja(SqlSession sqlSession, Caja caja, Date fechaCierre)
	        throws CajasServiceException, DocumentoException, ContadorNotFoundException, ContadorServiceException, MarshallUtilException, TicketsServiceException, UnsupportedEncodingException {
	    Date fechaCierreReal = new Date(); 
	    caja.setFechaCierre(fechaCierreReal);
		procesarMensajeRecuentosCaja(sqlSession, caja, fechaCierreReal);
		crearDocumentoCierreCaja(sqlSession, caja, fechaCierreReal);
		super.cerrarCaja(sqlSession, caja, fechaCierreReal);
	}
	

	public void crearDocumentoCierreCaja(SqlSession sqlSession, Caja caja, Date fechaCierre) throws DocumentoException,
	        ContadorNotFoundException, ContadorServiceException, MarshallUtilException, TicketsServiceException, CajasServiceException {
		Tienda tienda = sesion.getAplicacion().getTienda();
		ByLSesionAplicacion aplicacion = (ByLSesionAplicacion) sesion.getAplicacion();

		if (caja != null) {

			CierreCaja cierreCaja = new CierreCaja();
			cierreCaja.setUidActividad(aplicacion.getUidActividad());
			cierreCaja.setCodTicket(null);
			cierreCaja.setCodEmp(sesion.getAplicacion().getEmpresa().getCodEmpresa());
			cierreCaja.setUidTicket(UUID.randomUUID().toString());
			cierreCaja.setUidDiarioCaja(caja.getUidDiarioCaja());
			cierreCaja.setCodAlmacen(caja.getCodAlm());
			
			ZonedDateTime zonedApertura = caja.getFechaApertura().toInstant().atZone(ZoneId.systemDefault());
			cierreCaja.setFechaApertura(Date.from(zonedApertura.toInstant()));
			Date fechaSinMilis = quitarMilisegundos(caja.getFechaCierre());
			cierreCaja.setFecha(fechaSinMilis);
		    
			cierreCaja.setCodCaja(caja.getCodCaja());
			cierreCaja.setTurno(countTurnosCaja(caja));
			cierreCaja.setUsuario(caja.getUsuario());

			String moneda = "";
			try {
				PaisBean pais = paisService.consultarCodPais(tienda.getCliente().getCodpais());
				if (pais != null && StringUtils.isNotBlank(pais.getCodDivisa())) {
					moneda = pais.getCodDivisa();
				}
			}
			catch (PaisNotFoundException | PaisServiceException e) {
				log.error("crearDocumentoCierreCaja() - No se ha podido consultar la moneda del país durante la creación del documento de cierre de caja : " + e.getMessage(), e);
			}
			cierreCaja.setMoneda(moneda);

			// ---- TOTALES ----

			// Se refiere a contar todos los documentos que se han generado hasta ese cierre (No se cuenta el movmiento
			// de cierre).
			
			// El movimiento de apertura de caja solo se registra en D_CAJA_DET_TBL cuando el importe de apertura es mayor a 0.
			Integer transactionTotal = 0;
			Boolean cajaTieneApunteAperturaCaja = caja.getMovimientosApuntes().stream().anyMatch(apunte -> COD_CONCEPTO_MOV_APERTURA_CAJA.equals(apunte.getCodConceptoMovimiento()));
			if(!cajaTieneApunteAperturaCaja) {
				// Si no existe el registro, iniciamos el contador en 1 porque contamos con que la caja está abierta y se ha realizado el mov de apertura.
				transactionTotal = transactionTotal + 1;
			}
			
			// Comprobamos los recuentos, si hay un recuento anotado con importe 0 vendrá con decimales asi que lo tenemos en cuenta
			if(BigDecimalUtil.isMayorACero(caja.getTotalRecuento()) || caja.getTotalRecuento().scale() > 0) {
				transactionTotal++;
			}
			transactionTotal += caja.getMovimientosApuntes().size() + obtenerMovimientosCompletos(caja).size();
			cierreCaja.setTransactionTotal(transactionTotal);
			
			List<TipoOperacionVenta> customersSalesCount = new ArrayList<TipoOperacionVenta>();
			List<TipoOperacionVenta> salesCount = new ArrayList<TipoOperacionVenta>();
			Boolean paisTieneTr = true;
			
			// [BYL-347] - Se fuerza la excepcion de documento TR para aquellos países actualmente registrados y para futuros que se añadan
			// no tener que hacer comprobacion por codigo de pais
			try {
				documentos.getDocumento("TR");
				customersSalesCount = tipoOperacionVentaService.filterTipoOperacionesBy(caja, REGULAR, null, documentos.getDocumento("TR").getIdTipoDocumento(), false, paisTieneTr);
				// [BYL-384] - A partir de ahora se sumarán también las TR y cancelación de TR
				salesCount = tipoOperacionVentaService.filterTipoOperacionesBy(caja, SIMPLIFIED, null, 0L, false, paisTieneTr);
			}
			catch (DocumentoException e) {
				paisTieneTr = false;
				customersSalesCount = tipoOperacionVentaService.filterTipoOperacionesBy(caja, REGULAR, null, 0L, false, paisTieneTr);
				salesCount = tipoOperacionVentaService.filterTipoOperacionesBy(caja, SIMPLIFIED, null, 0L, false, paisTieneTr);
			}
			
			cierreCaja.setCustomerSalesCount(customersSalesCount.size());
			cierreCaja.setSalesCount(salesCount.size());
			
			Double giftcardsTotal = 0D, giftcardsOutTotal = 0D, returnsTotales = 0D, salesTotals = 0D, taxTotalPositive = 0D, taxTotalNegative = 0D;
			List<CajaMovimientoBean> movimientosVenta = caja.getMovimientosVenta();
			for (CajaMovimientoBean cajaMovimientoBean : movimientosVenta) {
				TipoDocumentoBean documento = documentos.getDocumento(cajaMovimientoBean.getIdTipoDocumento());
				try {
					CajaMovimientoTarjeta cajaMovimientoTarjeta = cajaMovimientoTarjetaService.consultar(cajaMovimientoBean);

					if ("TR".equals(documento.getCodtipodocumento())) {
						if (cajaMovimientoBean.getCargo().compareTo(BigDecimal.ZERO) >= 0) {
							// Suma total ventas tarjetas
							giftcardsTotal += cajaMovimientoBean.getCargo().abs().doubleValue();
							taxTotalPositive += cajaMovimientoTarjeta.getImpuestos().abs().doubleValue();
						}
						else {
							// Suma total cancelaciones tarjeta
							giftcardsOutTotal += cajaMovimientoBean.getCargo().abs().doubleValue();
							taxTotalNegative += cajaMovimientoTarjeta.getImpuestos().abs().doubleValue();
						}
					}
					else {
						PropiedadDocumentoBean propiedad = documento.getPropiedades().get(TIPOS_DOCUMENTOS_ORIGEN);
						if (propiedad != null) {
							if (StringUtils.isNotBlank(propiedad.getValor())) {
								// Suma Total Devoluciones (con impuestos)
								returnsTotales += cajaMovimientoBean.getAbono().abs().doubleValue();
								taxTotalNegative += cajaMovimientoTarjeta.getImpuestos().abs().doubleValue();
							}
						}
						else {
							// Suma Total Ventas (con impuestos)
							if(cajaMovimientoBean.getAbono().compareTo(BigDecimal.ZERO)>0) {
								salesTotals -= cajaMovimientoBean.getAbono().abs().doubleValue();
							}
							else {
								salesTotals += cajaMovimientoBean.getCargo().doubleValue();
							}
							if(cajaMovimientoTarjeta.getImpuestos().compareTo(BigDecimal.ZERO) < 0) {
								taxTotalNegative += cajaMovimientoTarjeta.getImpuestos().abs().doubleValue();
							}
							else {
								taxTotalPositive += cajaMovimientoTarjeta.getImpuestos().abs().doubleValue();
							}
							
						}
					}
				}
				catch (CajaMovimientoTarjetaException e) {
					log.warn("crearDocumentoCierreCaja() - No se ha encontrado el movmiento con número de línea " + cajaMovimientoBean.getLinea() + " de la caja "
					        + cajaMovimientoBean.getUidDiarioCaja() + " en la tabla 'x_caja_det_tbl'. Se seguirá con el próximo movimiento.");
				}
			}
			
			String tipoImpuesto = null;
			try {
				tipoImpuesto = ByLEmpresasRest.getTipoImpuestoEmpresa(sesion.getAplicacion().getUidActividad(), sesion.getAplicacion().getEmpresa().getCodEmpresa());
				log.debug("crearDocumentoCierreCaja() - tipo de impuesto de la empresa consultado: " + tipoImpuesto);
			}
			catch (RestException | RestHttpException e) {
				log.error("crearDocumentoCierreCaja() - No se ha podido consultar el tipo de impuesto de la empresa con código: " + sesion.getAplicacion().getEmpresa().getCodEmpresa()
				        + " durante la creación del documento de cierre de caja" + e.getMessage(), e);
			}
			
			if (StringUtils.isNotBlank(tipoImpuesto) && TAX_IN_SALE.equalsIgnoreCase(tipoImpuesto)) {
				cierreCaja.setTaxTotal(BigDecimal.ZERO);
			}else {
				cierreCaja.setTaxTotal(BigDecimalUtil.redondear(new BigDecimal(taxTotalPositive - taxTotalNegative), 2, BigDecimal.ROUND_HALF_UP));
			}
			
			cierreCaja.setGiftcardsTotals(BigDecimalUtil.redondear(new BigDecimal(giftcardsTotal), 2, BigDecimal.ROUND_HALF_UP));
			cierreCaja.setGiftcardsOutTotal(BigDecimalUtil.redondear(new BigDecimal(giftcardsOutTotal), 2, BigDecimal.ROUND_HALF_UP));

			cierreCaja.setReturnsTotales(BigDecimalUtil.redondear(new BigDecimal(-1D * returnsTotales), 2, BigDecimal.ROUND_HALF_UP));
			cierreCaja.setSalesTotals(BigDecimalUtil.redondear(new BigDecimal(salesTotals), 2, BigDecimal.ROUND_HALF_UP));

			if (cierreCaja != null) {
				Map<String, String> parametrosContador = new HashMap<>();

				parametrosContador.put("CODEMP", sesion.getAplicacion().getEmpresa().getCodEmpresa());
				parametrosContador.put("CODALM", sesion.getAplicacion().getTienda().getAlmacenBean().getCodAlmacen());
				parametrosContador.put("CODSERIE", sesion.getAplicacion().getTienda().getAlmacenBean().getCodAlmacen());
				parametrosContador.put("CODCAJA", sesion.getAplicacion().getCodCaja());
				parametrosContador.put("CODTIPODOCUMENTO", ByLDocumentos.CIERRE_CAJA_ERP);
				parametrosContador.put("PERIODO", ((new Fecha()).getAño().toString()));

				TipoDocumentoBean documento = sesion.getAplicacion().getDocumentos().getDocumento(ByLDocumentos.CIERRE_CAJA_ERP);
				ContadorBean ticketContador = null;
				if (documento != null) {
					ticketContador = servicioContadores.obtenerContador(documento.getIdContador(), parametrosContador, cierreCaja.getUidActividad());
				}
				String codTicket = servicioContadores.obtenerValorTotalConSeparador(ticketContador.getDivisor3(), ticketContador.getValorFormateado());
				cierreCaja.setCodTicket(codTicket);
				
				if (ticketContador != null) {
					TicketBean ticketBean = new TicketBean();
					ticketBean.setUidTicket(cierreCaja.getUidTicket());
					ticketBean.setCodAlmacen(cierreCaja.getCodAlmacen());
					ticketBean.setIdTicket(ticketContador.getValor());
					ticketBean.setFecha(fechaCierre);

					byte[] xmlAperturaCaja = MarshallUtil.crearXML(cierreCaja);
					ticketBean.setTicket(xmlAperturaCaja);

					ticketBean.setCodcaja(cierreCaja.getCodCaja());
					ticketBean.setIdTipoDocumento(documento.getIdTipoDocumento());
					ticketBean.setCodTicket(cierreCaja.getCodTicket());
					ticketBean.setFirma("*");
					ticketBean.setSerieTicket(ticketContador.getConfigContador().getValorDivisor3());

					ticketsService.insertarTicket(sqlSession, ticketBean, false);
					log.debug("crearDocumentoMovimientoCaja - Documento de movimiento de caja : " + cierreCaja.getUidDiarioCaja() + " creado satisfactoriamente");

				}
			}
		}
	}

	private Date quitarMilisegundos(Date fechaCierre) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(fechaCierre);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	private List<CajaMovimientoBean> obtenerMovimientosCompletos(Caja caja) {
		List<CajaMovimientoBean> nuevosMovimientos = caja.getMovimientosVenta();
		Set<String> idDocumentosSet = new HashSet<>();
		List<CajaMovimientoBean> movimientosVentas = new ArrayList<>();
		for (CajaMovimientoBean movimiento : nuevosMovimientos) {
		    if (!idDocumentosSet.contains(movimiento.getIdDocumento())) {
		        movimientosVentas.add(movimiento);
		        idDocumentosSet.add(movimiento.getIdDocumento());
		    }
		}
		return movimientosVentas;
	}
	
	private void procesarMensajeRecuentosCaja(SqlSession sqlSession, Caja caja, Date fechaCierre) throws TicketsServiceException, CajasServiceException {
	    try {
	        Tienda tienda = sesion.getAplicacion().getTienda();
	        TipoDocumentoBean documentoActivo = sesion.getAplicacion().getDocumentos().getDocumento(COD_DOCUMENTO_RECUENTO_CIERRE_CAJA);

	        log.debug("procesarMensajeRecuentosCaja() - Obteniendo contador para identificador...");
	        Map<String, String> parametrosContador = new HashMap<>();
	        parametrosContador.put("CODEMP", tienda.getAlmacenBean().getCodEmpresa());
	        parametrosContador.put("CODALM", tienda.getAlmacenBean().getCodAlmacen());
	        parametrosContador.put("CODSERIE", tienda.getAlmacenBean().getCodAlmacen());
	        parametrosContador.put("CODCAJA", caja.getCodCaja());
	        parametrosContador.put("CODTIPODOCUMENTO", documentoActivo.getCodtipodocumento());
	        parametrosContador.put("PERIODO", ((new Fecha(caja.getFechaApertura())).getAño().toString()));

	        ContadorBean ticketContador = servicioContadores.obtenerContador(documentoActivo.getIdContador(), parametrosContador, sesion.getAplicacion().getUidActividad());

	        String codTicket = servicioContadores.obtenerValorTotalConSeparador(ticketContador.getDivisor3(), ticketContador.getValorFormateado());
	        
	        TicketBean ticket = new TicketBean();
	        ticket.setCodAlmacen(tienda.getAlmacenBean().getCodAlmacen());
	        ticket.setCodcaja(caja.getCodCaja());
	        ticket.setFecha(fechaCierre);
	        ticket.setIdTicket(ticketContador.getValor());
	        ticket.setUidTicket(UUID.randomUUID().toString());
	        ticket.setIdTipoDocumento(documentoActivo.getIdTipoDocumento());
	        ticket.setSerieTicket(ticketContador.getDivisor3());
	        ticket.setCodTicket(codTicket);
	        ticket.setFirma("*");

	        RecuentoCajaBean recuentoCajaBean = new RecuentoCajaBean();
	        recuentoCajaBean.setUidActividad(sesion.getAplicacion().getUidActividad());
	        recuentoCajaBean.setUidtTicket(ticket.getUidTicket());
	        recuentoCajaBean.setCodTicket(ticket.getCodTicket());
	        recuentoCajaBean.setCodEmp(sesion.getAplicacion().getEmpresa().getCodEmpresa());
	        recuentoCajaBean.setUidDiarioCaja(caja.getUidDiarioCaja());
	        recuentoCajaBean.setCodAlmacen(tienda.getAlmacenBean().getCodAlmacen());
	        recuentoCajaBean.setCodCaja(caja.getCodCaja());
	        recuentoCajaBean.setFechaApertura(caja.getFechaApertura());
	        
	        ZonedDateTime zonedApertura = caja.getFechaApertura().toInstant().atZone(ZoneId.systemDefault());
	        recuentoCajaBean.setFechaApertura(Date.from(zonedApertura.toInstant()));
			Date fechaSinMilis = quitarMilisegundos(caja.getFechaCierre());
			recuentoCajaBean.setFecha(fechaSinMilis);
	        
	        recuentoCajaBean.setUsuario(caja.getUsuario());
	        recuentoCajaBean.setTurno(countTurnosCaja(caja));

	        String moneda = "";
	        try {
	            PaisBean pais = paisService.consultarCodPais(sesion.getAplicacion().getTienda().getCliente().getCodpais());
	            if (pais != null && StringUtils.isNotBlank(pais.getCodDivisa())) {
	                moneda = pais.getCodDivisa();
	            }
	        }
	        catch (PaisNotFoundException | PaisServiceException e) {
	            log.error("procesarMensajeRecuentosCaja() - No se ha podido consultar la moneda del país durante la creación del documento de recuento cierre de caja : " + e.getMessage(), e);
	        }
                recuentoCajaBean.setMoneda(moneda);
                List<RecuentoBean> recuentosBean = new ArrayList<>();

                for (CajaLineaRecuentoBean lineaRecuento : caja.getLineasRecuento()) {
                    MedioPagoBean medioPago = mediosPagosService.getMedioPago(lineaRecuento.getCodMedioPago());
                    if (!medioPago.getRecuentoAutomaticoCaja()) {
                        RecuentoBean recuentoBean = new RecuentoBean();
                        recuentoBean.setUidActividad(sesion.getAplicacion().getUidActividad());
                        recuentoBean.setCodMedioPago(lineaRecuento.getCodMedioPago());
                        recuentoBean.setCantidad(lineaRecuento.getCantidad());
                        recuentoBean.setValor(lineaRecuento.getValor().setScale(2));
                        recuentosBean.add(recuentoBean);
                    }
                }

                List<RecuentoBean> recuentosAgrupados = ByLRecuentoEfectivoAgrupador.agruparLineas(recuentosBean);
                recuentoCajaBean.setRecuentos(recuentosAgrupados);

                ticket.setTicket(MarshallUtil.crearXML(recuentoCajaBean));
	        log.debug("TICKET: " + ticket.getUidTicket() + "\n" + new String(ticket.getTicket(), "UTF-8") + "\n");

	        ticketsService.insertarTicket(sqlSession, ticket, false);
	    }
	    catch (DocumentoException | MarshallUtilException | UnsupportedEncodingException | ContadorServiceException e) {
	        throw new CajasServiceException(e.getMessage());
	    }
	}

	public Integer countTurnosCaja(Caja caja) {
	if (caja != null) {
		ZonedDateTime aperturaZdt = caja.getFechaApertura().toInstant().atZone(ZoneId.systemDefault());
		ZonedDateTime diaTienda;

		if (aperturaZdt.toLocalTime().isBefore(LocalTime.of(5, 0))) {
		    diaTienda = aperturaZdt.minusDays(1).toLocalDate().atStartOfDay(aperturaZdt.getZone());
		} else {
		    diaTienda = aperturaZdt.toLocalDate().atStartOfDay(aperturaZdt.getZone());
		}

		ZonedDateTime inicio = diaTienda.plusHours(5);
		ZonedDateTime fin = diaTienda.plusDays(1).plusHours(4).plusMinutes(59);

		Date fechaInicio = Date.from(inicio.toInstant());
		Date fechaFin = Date.from(fin.toInstant());

		CajaExample cajaExample = new CajaExample();
		cajaExample.or()
		    .andCodAlmacenEqualTo(caja.getCodAlm())
		    .andCodcajaEqualTo(caja.getCodCaja())
		    .andFechaAperturaBetween(fechaInicio, fechaFin);

		return cajaMapper.countByExample(cajaExample);
		
	}
	return 0;
}
	
	
	
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
	
	@Override
	public void crearMovimiento(SqlSession sqlSession, CajaMovimientoBean movimiento) throws CajasServiceException {
		try {
			String uidActividad = sesion.getAplicacion().getUidActividad();
			String uidDiarioCaja = sesion.getSesionCaja().getUidDiarioCaja();

			// Añadimos al objeto el uidActividad y el uidDiarioCaja de sesión
			movimiento.setUidActividad(uidActividad);
			movimiento.setUidDiarioCaja(uidDiarioCaja);
			movimiento.setUsuario(sesion.getSesionUsuario().getUsuario().getUsuario());

			if (movimiento.getLinea() == null) {
				Integer idLinea = consultarProximaLineaDetalleCaja(sqlSession);
				movimiento.setLinea(idLinea);
			}
			log.debug("crearMovimiento() - Insertando movimiento de caja");
			cajaMovimientoMapper.insert(movimiento);
			
			if (StringUtils.isNotBlank(movimiento.getCodConceptoMovimiento())) {
				/* En el caso de que sea un anticipo de reserva o una cancelacion no crearemos su movimiento de tipo CM */
				if (!ByLApartadosService.CODCONCEPTO_MOV_ANTICIPO_RESERVA.equals(movimiento.getCodConceptoMovimiento()) && !ByLApartadosService.CODCONCEPTO_MOV_DEVOLUCION_RESERVA.equals(movimiento.getCodConceptoMovimiento())) {
					Boolean isMovAperturaCaja = COD_CONCEPTO_MOV_APERTURA_CAJA.equals(movimiento.getCodConceptoMovimiento());
					if (!isMovAperturaCaja) {
						if (COD_CONCEPTO_MOV_CAJA_SALIDA_BANCO.equals(movimiento.getCodConceptoMovimiento())) {
							crearMovimientoCajaSalidaBanco(movimiento);
						}
						else {
							crearDocumentoMovimientoCaja(movimiento);
						}
					}
				}
			}
		}
		catch (Exception e) {
			String msg = "Se ha producido un error insertando movimiento de caja con código de pago: " + movimiento.getCodMedioPago() + " : " + e.getMessage();
			log.error("crearMovimiento() - " + msg, e);
			throw new CajasServiceException(I18N.getTexto("Error al insertar movimiento de caja"), e);
		}
	}
	
//	public CajaMovimientoBean crearMovimientoManual(BigDecimal importe, String codConcepto, String documento, String descConcepto) throws CajasServiceException {
//		log.debug("crearMovimientoManual() - Registrando movimiento manual por importe: " + importe + ". Y concepto: " + codConcepto);
//		SqlSession sqlSession = SpringContext.getBean(SpringTransactionSqlSession.class);
//		CajaMovimientoBean movimiento = new CajaMovimientoBean();
//		try {
//			sqlSession.openSession(SessionFactory.openSession());
//
//			if (BigDecimalUtil.isMayorACero(importe)) {
//				movimiento.setAbono(importe); // salida de caja
//			}
//			else {
//				movimiento.setCargo(importe.negate()); // entrada de caja
//			}
//			CajaConceptoBean concepto = cajaConceptosServices.getConceptoCaja(codConcepto);
//			if (concepto == null) {
//				log.error("crearMovimientoManual() - Se está intentando insertar un movimiento con concepto nulo. Código concepto: " + codConcepto);
//				throw new CajasServiceException(I18N.getTexto("Error al insertar movimiento de caja"));
//			}
//			concepto.setDesConceptoMovimiento(descConcepto);
//			movimiento.setCodConceptoMovimiento(concepto.getCodConceptoMovimiento());
//			movimiento.setConcepto(concepto.getDesConceptoMovimiento());
//			movimiento.setCodMedioPago(MediosPagosService.medioPagoDefecto.getCodMedioPago());
//			movimiento.setDocumento(documento);
//			movimiento.setFecha(new Date());
//			movimiento.setUsuario(sesion.getSesionUsuario().getUsuario().getUsuario());
//
//			crearMovimiento(sqlSession, movimiento);
//			if (COD_CONCEPTO_MOV_CAJA_SALIDA_BANCO.equals(codConcepto)) {
//				crearMovimientoCajaSalidaBanco(movimiento);
//			}
//			else {
//				crearDocumentoMovimientoCaja(movimiento);
//			}
//			sqlSession.commit();
//		}
//		catch (CajasServiceException e) {
//			sqlSession.rollback();
//			throw e;
//		}
//		catch (Exception e) {
//			sqlSession.rollback();
//			String msg = "Se ha producido un error insertando movimiento de caja por concepto: " + codConcepto + " : " + e.getMessage();
//			log.error("crearMovimientoManual() - " + msg, e);
//			throw new CajasServiceException(I18N.getTexto("Error al insertar movimiento de caja"), e);
//		}
//		finally {
//			sqlSession.close();
//		}
//
//		return movimiento;
//	}
	
	public void crearDocumentoMovimientoCaja(CajaMovimientoBean cajaMovimientoBean) {
		try {
			log.debug("crearDocumentoMovimientoCaja() - Creando documento de movimiento de caja para el movimiento " + cajaMovimientoBean.getUidTransaccionDet());

			Caja cajaAbierta = sesion.getSesionCaja().getCajaAbierta();

			if (cajaAbierta != null) {

				MovimientosCajaBean movimientosCajaBean = new MovimientosCajaBean();
				movimientosCajaBean.setUidActividad(cajaMovimientoBean.getUidActividad());
				movimientosCajaBean.setUidDiarioCaja(cajaMovimientoBean.getUidDiarioCaja());
				movimientosCajaBean.setUidTicket(UUID.randomUUID().toString());
				movimientosCajaBean.setCodEmp(sesion.getAplicacion().getEmpresa().getCodEmpresa());
				movimientosCajaBean.setCodAlmacen(cajaAbierta.getCodAlm());
				movimientosCajaBean.setCodCaja(cajaAbierta.getCodCaja());
				movimientosCajaBean.setCodConceptoMov(cajaMovimientoBean.getCodConceptoMovimiento());
				movimientosCajaBean.setFechaMovimiento(cajaMovimientoBean.getFecha());
				movimientosCajaBean.setUsuario(cajaMovimientoBean.getUsuario());
				movimientosCajaBean.setTurno(countTurnosCaja(cajaAbierta));
				
				if (cajaMovimientoBean.getCargo() != null) {
					movimientosCajaBean.setCargo(cajaMovimientoBean.getCargo());
				}
				if (cajaMovimientoBean.getAbono() != null) {
					movimientosCajaBean.setAbono(cajaMovimientoBean.getAbono());
				}

				try {
					String moneda = null;
					PaisBean pais = paisService.consultarCodPais(sesion.getAplicacion().getTienda().getCliente().getCodpais());
					if (pais != null && StringUtils.isNotBlank(pais.getCodDivisa())) {
						moneda = pais.getCodDivisa();
					}
					movimientosCajaBean.setMoneda(moneda);
				}
				catch (PaisNotFoundException | PaisServiceException e) {
					log.error("crearDocumentoMovimientoCaja() - No se ha podido consultar la moneda del país durante la creación del documento de movimiento de caja : " + e.getMessage(), e);
				}
				
				if (cajaMovimientoBean.getDocumento() != null) {
					movimientosCajaBean.setDocumento(cajaMovimientoBean.getDocumento());
				}

				if (movimientosCajaBean != null) {

					Map<String, String> parametrosContador = new HashMap<>();

					parametrosContador.put("CODEMP", sesion.getAplicacion().getEmpresa().getCodEmpresa());
					parametrosContador.put("CODALM", sesion.getAplicacion().getTienda().getAlmacenBean().getCodAlmacen());
					parametrosContador.put("CODSERIE", sesion.getAplicacion().getTienda().getAlmacenBean().getCodAlmacen());
					parametrosContador.put("CODCAJA", sesion.getAplicacion().getCodCaja());
					parametrosContador.put("CODTIPODOCUMENTO", ByLDocumentos.MOVIMIENTO_CAJA_ERP);
					parametrosContador.put("PERIODO", ((new Fecha()).getAño().toString()));

					TipoDocumentoBean documento = sesion.getAplicacion().getDocumentos().getDocumento(ByLDocumentos.MOVIMIENTO_CAJA_ERP);
					ContadorBean ticketContador = null;
					if (documento != null) {
						ticketContador = servicioContadores.obtenerContador(documento.getIdContador(), parametrosContador, movimientosCajaBean.getUidActividad());
					}
					String codTicket = servicioContadores.obtenerValorTotalConSeparador(ticketContador.getDivisor3(), ticketContador.getValorFormateado());

					if (ticketContador != null) {
						TicketBean ticketBean = new TicketBean();
						ticketBean.setUidTicket(movimientosCajaBean.getUidTicket());
						ticketBean.setCodAlmacen(movimientosCajaBean.getCodAlmacen());
						ticketBean.setIdTicket(ticketContador.getValor());
						ticketBean.setFecha(cajaMovimientoBean.getFecha());
						
						movimientosCajaBean.setCodTicket(codTicket);
						byte[] xmlAperturaCaja = MarshallUtil.crearXML(movimientosCajaBean);
						ticketBean.setTicket(xmlAperturaCaja);

						ticketBean.setCodcaja(movimientosCajaBean.getCodCaja());
						ticketBean.setIdTipoDocumento(documento.getIdTipoDocumento());
						ticketBean.setCodTicket(codTicket);
						ticketBean.setFirma("*");
						ticketBean.setSerieTicket(ticketContador.getConfigContador().getValorDivisor3());

						SqlSession sqlSession = SpringContext.getBean(SpringTransactionSqlSession.class);
						ticketsService.insertarTicket(sqlSession, ticketBean, false);
						log.debug("crearDocumentoMovimientoCaja - Documento de movimiento de caja : " + cajaAbierta.getUidDiarioCaja() + " creado satisfactoriamente");
					}
				}
			}
		}
		catch (Exception e) {
			log.error("crearDocumentoMovimientoCaja() - No se ha podido crear el documento de movimiento para : " + cajaMovimientoBean.getUidTransaccionDet());
		}
	}
	
	public void crearMovimientoCajaSalidaBanco(CajaMovimientoBean cajaMovimientoBean) {
		try {
			log.debug("crearMovimientoCajaSalidaBanco() - Creando documento de movimiento de caja de salida de banco para el movimiento " + cajaMovimientoBean.getUidTransaccionDet());

			Caja cajaAbierta = sesion.getSesionCaja().getCajaAbierta();

			if (cajaAbierta != null) {

				MovimientosCajaSalidaBancoBean movimientosCajaSalidaBancoBean = new MovimientosCajaSalidaBancoBean();
				movimientosCajaSalidaBancoBean.setUidActividad(cajaMovimientoBean.getUidActividad());
				movimientosCajaSalidaBancoBean.setUidDiarioCaja(cajaMovimientoBean.getUidDiarioCaja());
				movimientosCajaSalidaBancoBean.setUidTicket(UUID.randomUUID().toString());
				movimientosCajaSalidaBancoBean.setCodEmp(sesion.getAplicacion().getEmpresa().getCodEmpresa());
				movimientosCajaSalidaBancoBean.setCodAlmacen(cajaAbierta.getCodAlm());
				movimientosCajaSalidaBancoBean.setCodCaja(cajaAbierta.getCodCaja());
				movimientosCajaSalidaBancoBean.setCodConceptoMov(cajaMovimientoBean.getCodConceptoMovimiento());
				movimientosCajaSalidaBancoBean.setFechaMovimiento(cajaMovimientoBean.getFecha());
				movimientosCajaSalidaBancoBean.setUsuario(cajaMovimientoBean.getUsuario());
				movimientosCajaSalidaBancoBean.setTurno(countTurnosCaja(cajaAbierta));
				
				if (cajaMovimientoBean.getCargo() != null) {
					movimientosCajaSalidaBancoBean.setCargo(cajaMovimientoBean.getCargo());
				}
				if (cajaMovimientoBean.getAbono() != null) {
					movimientosCajaSalidaBancoBean.setAbono(cajaMovimientoBean.getAbono());
				}

				try {
					String moneda = null;
					PaisBean pais = paisService.consultarCodPais(sesion.getAplicacion().getTienda().getCliente().getCodpais());
					if (pais != null && StringUtils.isNotBlank(pais.getCodDivisa())) {
						moneda = pais.getCodDivisa();
					}
					movimientosCajaSalidaBancoBean.setMoneda(moneda);
				}
				catch (PaisNotFoundException | PaisServiceException e) {
					log.error("crearMovimientoCajaSalidaBanco() - No se ha podido consultar la moneda del país durante la creación del documento de movimiento de caja de salida de banco : " + e.getMessage(), e);
				}
				
				if (cajaMovimientoBean.getDocumento() != null) {
					movimientosCajaSalidaBancoBean.setDocumento(cajaMovimientoBean.getDocumento());
				}

				if (movimientosCajaSalidaBancoBean != null) {

					Map<String, String> parametrosContador = new HashMap<>();

					parametrosContador.put("CODEMP", sesion.getAplicacion().getEmpresa().getCodEmpresa());
					parametrosContador.put("CODALM", sesion.getAplicacion().getTienda().getAlmacenBean().getCodAlmacen());
					parametrosContador.put("CODSERIE", sesion.getAplicacion().getTienda().getAlmacenBean().getCodAlmacen());
					parametrosContador.put("CODCAJA", sesion.getAplicacion().getCodCaja());
					parametrosContador.put("CODTIPODOCUMENTO", ByLDocumentos.MOVIMIENTO_CAJA_SALIDA_BANCO_ERP);
					parametrosContador.put("PERIODO", ((new Fecha()).getAño().toString()));

					TipoDocumentoBean documento = sesion.getAplicacion().getDocumentos().getDocumento(ByLDocumentos.MOVIMIENTO_CAJA_SALIDA_BANCO_ERP);
					ContadorBean ticketContador = null;
					if (documento != null) {
						ticketContador = servicioContadores.obtenerContador(documento.getIdContador(), parametrosContador, movimientosCajaSalidaBancoBean.getUidActividad());
					}
					String codTicket = servicioContadores.obtenerValorTotalConSeparador(ticketContador.getDivisor3(), ticketContador.getValorFormateado());

					if (ticketContador != null) {
						TicketBean ticketBean = new TicketBean();
						ticketBean.setUidTicket(movimientosCajaSalidaBancoBean.getUidTicket());
						ticketBean.setCodAlmacen(movimientosCajaSalidaBancoBean.getCodAlmacen());
						ticketBean.setIdTicket(ticketContador.getValor());
						ticketBean.setFecha(new Date());

						movimientosCajaSalidaBancoBean.setCodTicket(codTicket);
						byte[] xmlAperturaCaja = MarshallUtil.crearXML(movimientosCajaSalidaBancoBean);
						ticketBean.setTicket(xmlAperturaCaja);

						ticketBean.setCodcaja(movimientosCajaSalidaBancoBean.getCodCaja());
						ticketBean.setIdTipoDocumento(documento.getIdTipoDocumento());
						ticketBean.setCodTicket(codTicket);
						ticketBean.setFirma("*");
						ticketBean.setSerieTicket(ticketContador.getConfigContador().getValorDivisor3());

						SqlSession sqlSession = SpringContext.getBean(SpringTransactionSqlSession.class);
						ticketsService.insertarTicket(sqlSession, ticketBean, false);
						log.debug("crearMovimientoCajaSalidaBanco - Documento de movimiento de caja de salida de banco : " + cajaAbierta.getUidDiarioCaja() + " creado satisfactoriamente");
					}
				}
			}
		}
		catch (Exception e) {
			log.error("crearMovimientoCajaSalidaBanco() - No se ha podido crear el documento de movimiento de salida de banco para : " + cajaMovimientoBean.getUidTransaccionDet());
		}
	}

	public void crearDocumentoAnticipoReserva(TicketReserva ticketReserva) {
		try {
			log.debug("crearDocumentoAnticipoReserva() - Creando documento de anticipo para la reserva " + ticketReserva.getCabecera().getIdTicket());
			Caja cajaAbierta = sesion.getSesionCaja().getCajaAbierta();
			if (cajaAbierta != null) {

				MensajeAnticipoReservaBean mensajeAnticipoReserva = new MensajeAnticipoReservaBean();

				MensajeAnticipoReservaCabeceraBean mensajeAnticipoReservaCabecera = new MensajeAnticipoReservaCabeceraBean();
				mensajeAnticipoReserva.setCabecera(mensajeAnticipoReservaCabecera);

				mensajeAnticipoReservaCabecera.setUidActividad(ticketReserva.getCabecera().getUidActividad());
				mensajeAnticipoReservaCabecera.setUidTicket(UUID.randomUUID().toString());
				mensajeAnticipoReservaCabecera.setCodEmp(sesion.getAplicacion().getEmpresa().getCodEmpresa());
				mensajeAnticipoReservaCabecera.setCodAlmacen(sesion.getAplicacion().getCodAlmacen());
				mensajeAnticipoReservaCabecera.setCodCaja(sesion.getAplicacion().getCodCaja());
				mensajeAnticipoReservaCabecera.setFecha(ticketReserva.getCabecera().getFecha());
				mensajeAnticipoReservaCabecera.setTurno(countTurnosCaja(cajaAbierta));
				if (ticketReserva.getCabecera().getEsPagoAnticipo().equals("S")) {
					mensajeAnticipoReservaCabecera.setCodConceptoMov("22");
				}
				else if (ticketReserva.getCabecera().getEsCancelacion().equals("S")) {
					mensajeAnticipoReservaCabecera.setCodConceptoMov("23");
				}

				try {
					String moneda = null;
					PaisBean pais = paisService.consultarCodPais(sesion.getAplicacion().getTienda().getCliente().getCodpais());
					if (pais != null && StringUtils.isNotBlank(pais.getCodDivisa())) {
						moneda = pais.getCodDivisa();
					}
					mensajeAnticipoReservaCabecera.setMoneda(moneda);
				}
				catch (PaisNotFoundException | PaisServiceException e) {
					log.error("crearDocumentoAnticipoReserva() - No se ha podido consultar la moneda del país durante la creación del documento de movimiento anticipo de reserva : " + e.getMessage(),
					        e);
				}
				
				if(ticketReserva.getCabecera().getDatosFidelizado() != null) {
					DatosFidelizadoReservaTicket fidelizacion = new DatosFidelizadoReservaTicket();
					fidelizacion = ticketReserva.getCabecera().getDatosFidelizado();
					mensajeAnticipoReservaCabecera.setDatosFidelizado(fidelizacion);
				}
				mensajeAnticipoReservaCabecera.setCliente(ticketReserva.getCabecera().getCliente());
				
				mensajeAnticipoReserva.setPagos(obtenerUltimosPagosAnticipoReserva(ticketReserva.getPagos()));

				if (mensajeAnticipoReserva != null && mensajeAnticipoReserva.getCabecera() != null) {

					Map<String, String> parametrosContador = new HashMap<>();

					parametrosContador.put("CODEMP", sesion.getAplicacion().getEmpresa().getCodEmpresa());
					parametrosContador.put("CODALM", sesion.getAplicacion().getTienda().getAlmacenBean().getCodAlmacen());
					parametrosContador.put("CODSERIE", sesion.getAplicacion().getTienda().getAlmacenBean().getCodAlmacen());
					parametrosContador.put("CODCAJA", sesion.getAplicacion().getCodCaja());
					parametrosContador.put("CODTIPODOCUMENTO", ByLDocumentos.ANTICIPO_RESERVA_ERP);
					parametrosContador.put("PERIODO", ((new Fecha()).getAño().toString()));

					TipoDocumentoBean documento = sesion.getAplicacion().getDocumentos().getDocumento(ByLDocumentos.ANTICIPO_RESERVA_ERP);
					ContadorBean ticketContador = null;
					if (documento != null) {
						ticketContador = servicioContadores.obtenerContador(documento.getIdContador(), parametrosContador, mensajeAnticipoReserva.getCabecera().getUidActividad());
					}
					String codTicket = servicioContadores.obtenerValorTotalConSeparador(ticketContador.getDivisor3(), ticketContador.getValorFormateado());

					if (ticketContador != null) {
						TicketBean ticketBean = new TicketBean();
						ticketBean.setUidTicket(mensajeAnticipoReserva.getCabecera().getUidTicket());
						ticketBean.setCodAlmacen(mensajeAnticipoReserva.getCabecera().getCodAlmacen());
						ticketBean.setIdTicket(ticketContador.getValor());
						ticketBean.setFecha(new Date());

						mensajeAnticipoReserva.getCabecera().setCodTicket(codTicket);
						byte[] xmlAperturaCaja = MarshallUtil.crearXML(mensajeAnticipoReserva);
						ticketBean.setTicket(xmlAperturaCaja);

						ticketBean.setCodcaja(mensajeAnticipoReserva.getCabecera().getCodCaja());
						ticketBean.setIdTipoDocumento(documento.getIdTipoDocumento());
						ticketBean.setCodTicket(codTicket);
						ticketBean.setFirma("*");
						ticketBean.setSerieTicket(ticketContador.getConfigContador().getValorDivisor3());

						SqlSession sqlSession = SpringContext.getBean(SpringTransactionSqlSession.class);
						ticketsService.insertarTicket(sqlSession, ticketBean, false);
						log.debug("crearDocumentoAnticipoReserva - Documento de anticipo de reserva : " + mensajeAnticipoReserva.getCabecera().getUidTicket() + " creado satisfactoriamente");
					}
				}
			}
		}
		catch (Exception e) {
			log.error("crearDocumentoAnticipoReserva() - No se ha podido crear el documento documento de anticipo para la reserva " + ticketReserva.getCabecera().getIdTicket());
		}
	}

	protected List<PagoReservaTicket> obtenerUltimosPagosAnticipoReserva(List<PagoReservaTicket> listaPagos) {
		List<PagoReservaTicket> ultimosPagos = new ArrayList<>();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Date fechaUltimoPago = null;
		Integer contLineas = null;
		for (PagoReservaTicket pagoReserva : listaPagos) {
			try {
				PagoReservaTicket pagoAux = new PagoReservaTicket();
				BeanUtils.copyProperties(pagoReserva, pagoAux);
				Date fechaActualizacion = dateFormat.parse(pagoReserva.getFechaActualizacion());

				if (fechaUltimoPago == null || fechaActualizacion.after(fechaUltimoPago)) {
					fechaUltimoPago = fechaActualizacion;

					// Limpiamos la lista de los últimos pagos y agregamos el pago actual
					ultimosPagos.clear();
					contLineas = 1;
					pagoAux.setLinea(contLineas.toString());
					ultimosPagos.add(pagoAux);
					contLineas++;
				}
				else if (fechaUltimoPago.equals(fechaActualizacion)) {
					pagoAux.setLinea(contLineas.toString());
					ultimosPagos.add(pagoAux);
					contLineas++;
				}
			}
			catch (ParseException e) {
				log.error("obtenerUltimosPagosAnticipoReserva() - No se ha podido añadir el pago " + pagoReserva.getCodigoMedioPago());
			}
		}

		return ultimosPagos;
	}
}
