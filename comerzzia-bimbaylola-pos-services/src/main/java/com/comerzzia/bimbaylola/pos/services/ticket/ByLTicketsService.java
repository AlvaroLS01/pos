package com.comerzzia.bimbaylola.pos.services.ticket;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.bimbaylola.pos.devices.impresoras.fiscal.IFiscalPrinter;
import com.comerzzia.bimbaylola.pos.persistence.core.config.configcontadores.ByLConfigContadorBean;
import com.comerzzia.bimbaylola.pos.persistence.core.config.configcontadores.rangos.ConfigContadorRango;
import com.comerzzia.bimbaylola.pos.persistence.core.config.configcontadores.rangos.ConfigContadorRangoExample;
import com.comerzzia.bimbaylola.pos.persistence.core.contadores.ByLContadorBean;
import com.comerzzia.bimbaylola.pos.persistence.movimientostarjeta.CajaMovimientoTarjeta;
import com.comerzzia.bimbaylola.pos.persistence.tickets.ByLTicketBean;
import com.comerzzia.bimbaylola.pos.persistence.tickets.ByLTicketExample;
import com.comerzzia.bimbaylola.pos.persistence.tickets.ByLTicketMapper;
import com.comerzzia.bimbaylola.pos.services.cajas.ByLCajasService;
import com.comerzzia.bimbaylola.pos.services.core.ByLServicioContadores;
import com.comerzzia.bimbaylola.pos.services.core.config.configContadores.ByLServicioConfigContadoresImpl;
import com.comerzzia.bimbaylola.pos.services.core.config.configContadores.rangos.CounterRangeManager;
import com.comerzzia.bimbaylola.pos.services.core.config.configContadores.rangos.CounterRangeParamDto;
import com.comerzzia.bimbaylola.pos.services.core.config.configContadores.rangos.ServicioConfigContadoresRangos;
import com.comerzzia.bimbaylola.pos.services.core.documentos.ByLDocumentos;
import com.comerzzia.bimbaylola.pos.services.core.procesadorIdFiscal.IProcesadorFiscal;
import com.comerzzia.bimbaylola.pos.services.core.procesadorIdFiscal.ProcesadorIdFiscalException;
import com.comerzzia.bimbaylola.pos.services.core.procesadorIdFiscal.ProcesarDocumentoFiscalPAException;
import com.comerzzia.bimbaylola.pos.services.core.variables.ByLVariablesServices;
import com.comerzzia.bimbaylola.pos.services.movimientos.CajaMovimientoTarjetaService;
import com.comerzzia.bimbaylola.pos.services.movimientos.exception.CajaMovimientoTarjetaConstraintViolationException;
import com.comerzzia.bimbaylola.pos.services.movimientos.exception.CajaMovimientoTarjetaException;
import com.comerzzia.bimbaylola.pos.services.taxFree.TaxFreeLeerXML;
import com.comerzzia.bimbaylola.pos.services.taxFree.TaxFreeProcesador;
import com.comerzzia.bimbaylola.pos.services.taxFree.TaxFreeXMLVoucher;
import com.comerzzia.bimbaylola.pos.services.ticket.aparcados.reservas.DatosRespuestaTarjetaReservaTicket;
import com.comerzzia.bimbaylola.pos.services.ticket.cabecera.ByLCabeceraTicket;
import com.comerzzia.bimbaylola.pos.services.ticket.profesional.ByLLineaTicketProfesional;
import com.comerzzia.bimbaylola.pos.services.ventas.tipooperacion.TipoOperacionVentaService;
import com.comerzzia.bimbaylola.pos.services.vertex.VertexService;
import com.comerzzia.core.model.notificaciones.ContactoModel;
import com.comerzzia.core.model.notificaciones.DestinatarioModel;
import com.comerzzia.core.model.notificaciones.NotificacionModel;
import com.comerzzia.core.util.fechas.Fecha;
import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.core.util.tipoidentificacion.ValidadorDocumentoIdentificacionException;
import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.core.dispositivos.dispositivo.impresora.IPrinter;
import com.comerzzia.pos.persistence.cajas.movimientos.CajaMovimientoBean;
import com.comerzzia.pos.persistence.core.contadores.ContadorBean;
import com.comerzzia.pos.persistence.core.documentos.propiedades.PropiedadDocumentoBean;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.persistence.mediosPagos.MedioPagoBean;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.persistence.mybatis.SpringTransactionSqlSession;
import com.comerzzia.pos.persistence.paises.PaisBean;
import com.comerzzia.pos.persistence.tickets.TicketBean;
import com.comerzzia.pos.persistence.tickets.TicketExample;
import com.comerzzia.pos.services.cajas.CajasServiceException;
import com.comerzzia.pos.services.core.config.configContadores.ServicioConfigContadores;
import com.comerzzia.pos.services.core.contadores.ContadorNotFoundException;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.core.listeners.ListenersExecutor;
import com.comerzzia.pos.services.core.listeners.tipos.ticket.SalvadoTicketListener;
import com.comerzzia.pos.services.core.paises.PaisNotFoundException;
import com.comerzzia.pos.services.core.paises.PaisService;
import com.comerzzia.pos.services.core.paises.PaisServiceException;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.fiscaldata.FiscalData;
import com.comerzzia.pos.services.fiscaldata.FiscalDataException;
import com.comerzzia.pos.services.fiscaldata.FiscalDataService;
import com.comerzzia.pos.services.mediospagos.MediosPagosService;
import com.comerzzia.pos.services.ticket.ITicket;
import com.comerzzia.pos.services.ticket.Ticket;
import com.comerzzia.pos.services.ticket.TicketVenta;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.services.ticket.TicketsService;
import com.comerzzia.pos.services.ticket.TicketsServiceException;
import com.comerzzia.pos.services.ticket.cabecera.FirmaTicket;
import com.comerzzia.pos.services.ticket.cabecera.SubtotalIvaTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.pagos.EntregaCuentaTicket;
import com.comerzzia.pos.services.ticket.pagos.IPagoTicket;
import com.comerzzia.pos.services.ticket.pagos.PagoTicket;
import com.comerzzia.pos.services.ticket.profesional.LineaTicketProfesional;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;
import com.comerzzia.pos.util.xml.MarshallUtil;
import com.comerzzia.pos.util.xml.MarshallUtilException;

@Component
@Primary
public class ByLTicketsService extends TicketsService {

    private static final String PROPIEDAD_CLASE_PROCESAMIENTO_ID_FISCAL = "CLASE_PROCESAMIENTO.ID_FISCAL";

	protected static final Logger log = Logger.getLogger(ByLTicketsService.class);

	public static final String CONTADOR_TRANSACCION = "ID_TRANSACCION";
	public static final String CONTADOR_FIRMA_ADYEN = "ID_FIRMA_ADYEN";
	public static final String CLASE_PROCESAMIENTO_IDFISCAL = PROPIEDAD_CLASE_PROCESAMIENTO_ID_FISCAL;
	public static final String CONTADOR_ADICIONAL = "CLASE_PROCESAMIENTO.CONTADOR_ADICIONAL";
	protected static final String EXTENSION_RANGE_ID = "RANGE_ID";
	
	private static final String VARIABLE_CORREO_ENVIO_FIN_CONTADOR = "NOTIFICACION.CORREO_FIN_CONTADOR";
	public final static String NOTIFICACION_CONTADOR = "NOTIFICACION_CONTADOR";
	protected static final String PROPIEDAD_CONTADOR_MX = "POS.SEPARADOR_MEXICO";
	
	public static final String EC_DEFAULT_NOMBRE_RAZON_SOCIAL = "CONSUMIDOR FINAL";
	public static final String CREAR_CLAVE_ACCESO_WEBPOS = "CREAR_CLAVE_ACCESO_WEBPOS";
	
	public static final String TIPO_PROPORCIONAL_BASE = "BASE";
	public static final String TIPO_PROPORCIONAL_IMPUESTOS = "IMPUESTOS";
	
    @Autowired
    private ListenersExecutor listenersExecutor;
    
    @Autowired
    protected ByLServicioContadores byLServicioContadores;
   
    @Autowired
    protected ServicioConfigContadores servicioConfigContadores;
    
    @Autowired
    protected ByLDocumentos documentos;
    
	@Autowired
    protected VariablesServices variablesService;
	
	@Autowired
	protected VertexService vertexService;
	
	@Autowired
	protected ByLCajasService cajasService;
	
	@Autowired
	protected TipoOperacionVentaService tipoOperacionVentaService;
	
	@Autowired
	protected CajaMovimientoTarjetaService movimientoTarjetaService;

	@Autowired
	private ApplicationContext context;
	
	@Autowired
	protected ByLTicketMapper ByLTicketMapper;
	
	@Autowired
    protected CounterRangeManager counterRangeManager;
	
	@Autowired
	protected PaisService paisService;
	
	
	protected Date fechaOrigenSinTicketReferenciar;
	
	public void setFechaOrigenSinTicketReferenciar(Date fechaOrigenSinTicketReferenciar) {
		this.fechaOrigenSinTicketReferenciar = fechaOrigenSinTicketReferenciar;
	}
	
	public Date getFechaOrigenSinTicketReferenciar() {
		return this.fechaOrigenSinTicketReferenciar;
	}
	
	@SuppressWarnings("rawtypes")
	public synchronized void setContadorIdTicket(Ticket ticket) throws TicketsServiceException {
		log.debug("setContadorIdTicket() - Obteniendo contador para identificador...");

		try {
			
			Map<String, String> parametrosContador = new HashMap<>();
			Map<String, String> condicionesVigencias = new HashMap<>();

			parametrosContador.put("CODEMP", ticket.getEmpresa().getCodEmpresa());
			parametrosContador.put("CODALM", ticket.getTienda().getAlmacenBean().getCodAlmacen());
			parametrosContador.put("CODSERIE", ticket.getTienda().getAlmacenBean().getCodAlmacen());
			parametrosContador.put("CODCAJA", ticket.getCodCaja());
			parametrosContador.put("CODTIPODOCUMENTO", ticket.getCabecera().getCodTipoDocumento());
			parametrosContador.put("PERIODO", ((new Fecha()).getAño().toString()));

			// Se añaden vigencias para los rangos
			condicionesVigencias.put(ConfigContadorRango.VIGENCIA_CODCAJA, ticket.getCabecera().getCodCaja());
			condicionesVigencias.put(ConfigContadorRango.VIGENCIA_CODALM, ticket.getCabecera().getTienda().getCodAlmacen());
			condicionesVigencias.put(ConfigContadorRango.VIGENCIA_CODEMP, ticket.getCabecera().getEmpresa().getCodEmpresa());

			TipoDocumentoBean documentoActivo = sesion.getAplicacion().getDocumentos().getDocumento(ticket.getCabecera().getCodTipoDocumento());

			// Tratamos los rangos en caso de que los tenga
			ByLConfigContadorBean confContador = (ByLConfigContadorBean) ByLServicioConfigContadoresImpl.get().consultar(documentoActivo.getIdContador());
			if (!confContador.isRangosCargados()) {
				ConfigContadorRangoExample example = new ConfigContadorRangoExample();
				example.or().andIdContadorEqualTo(confContador.getIdContador());
				example.setOrderByClause(ConfigContadorRangoExample.ORDER_BY_RANGO_INICIO + ", " + ConfigContadorRangoExample.ORDER_BY_RANGO_FIN + ", "
				        + ConfigContadorRangoExample.ORDER_BY_RANGO_FECHA_INICIO + ", " + ConfigContadorRangoExample.ORDER_BY_RANGO_FECHA_FIN);
				List<ConfigContadorRango> rangos = ServicioConfigContadoresRangos.get().consultar(example);

				confContador.setRangos(rangos);
				confContador.setRangosCargados(true);
			}

			ByLContadorBean ticketContador = byLServicioContadores.consultarContadorActivo(confContador, parametrosContador, condicionesVigencias, ticket.getUidActividad(), true);
			if (ticketContador == null || ticketContador.getError() != null) {
				throw new ContadorNotFoundException("No se ha encontrado un contador disponible");
			}
			
			String codTicket = "";
			// En caso de que la venta/devolución sea en México, hay que cambiar la barra del separador por un guión
			// Para ello, usamos las propiedades de los documentos que tienen asignado un contador personalizado para Mexico, el resto de documentos usan los contadores estandar
			PropiedadDocumentoBean propiedadContadorMX = documentos.getDocumento(ticket.getCabecera().getTipoDocumento()).getPropiedades().get(PROPIEDAD_CONTADOR_MX);
			if (propiedadContadorMX != null && !propiedadContadorMX.getValor().isEmpty()) {
				String separador = propiedadContadorMX.getValor();
				codTicket = byLServicioContadores.obtenerValorTotalConSeparadorMX(ticketContador.getConfigContador().getValorDivisor3(), ticketContador.getValorFormateado(), separador);
			} else {
				codTicket = servicioContadores.obtenerValorTotalConSeparador(ticketContador.getConfigContador().getValorDivisor3(), ticketContador.getValorFormateado());	
			}		
			
			ticket.getCabecera().setSerieTicket(ticketContador.getConfigContador().getValorDivisor3());
			ticket.getCabecera().setCodTicket(codTicket);
			ticket.getCabecera().setUidTicket(UUID.randomUUID().toString());

			((ByLCabeceraTicket) ticket.getCabecera()).setContador(ticketContador);
			
			// Petición de identificación fiscal (numero Folio en el caso de Chile)
			PropiedadDocumentoBean propiedadClaseProcesamiento = documentos.getDocumento(ticket.getCabecera().getTipoDocumento()).getPropiedades().get(CLASE_PROCESAMIENTO_IDFISCAL);
			if (propiedadClaseProcesamiento != null && !propiedadClaseProcesamiento.getValor().isEmpty()) {
				IProcesadorFiscal procesadorIdFiscal;
				Class<?> clazz = null;

				clazz = Class.forName(propiedadClaseProcesamiento.getValor());

				try {
					procesadorIdFiscal =  (IProcesadorFiscal) context.getBean(clazz);
				}
				catch (Exception e) {
					procesadorIdFiscal = (IProcesadorFiscal) clazz.newInstance();
				}
				
				String identificadorFiscal = procesadorIdFiscal.obtenerIdFiscal(ticket);
				((ByLCabeceraTicket) ticket.getCabecera()).setIdentificadorFiscal(identificadorFiscal);
			}
			
			// Hacemos el seteo del idTicket al final para que sólo lo setee en el caso de que todo haya ido por OK.
			// De esta manera, si falla el contador de ID_FISCAL, no lo seteará y volverá a entrar de nuevo en este método (setContadorIdTicket)
			ticket.setIdTicket(ticketContador.getValor());
			
			/* Rango de contadores de ATCUD */
			CounterRangeParamDto counterRangeParam = new CounterRangeParamDto();
        	counterRangeParam.setCounterId(ticketContador.getIdContador());
        	counterRangeParam.setDivisor1(ticketContador.getDivisor1());
        	counterRangeParam.setDivisor2(ticketContador.getDivisor2());
        	counterRangeParam.setDivisor3(ticketContador.getDivisor3());
        	
        	String rangeId = counterRangeManager.findRangeId(counterRangeParam);
        	
        	if(StringUtils.isNotBlank(rangeId)) {
        		((TicketVentaAbono) ticket).addExtension(EXTENSION_RANGE_ID, rangeId);
        	}
		}
		catch (Exception e) {
			String msg = "Se ha producido un error procesando ticket con uid " + ticket.getUidTicket() + " : " + e.getMessage();
			log.error("registrarTicket() - " + msg, e);
			throw new TicketsServiceException(e);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public synchronized void registrarTicket(Ticket ticket, TipoDocumentoBean tipoDocumento, boolean procesarTicket, SqlSession sqlSession) throws Exception {
		log.debug("registrarTicket() - Procesando ticket ... ");

		byte[] xmlTicket = null;
		TicketBean ticketBean;
		// Establecemos fecha del ticket
		ticket.setFecha(new Date());

		IPrinter printer = Dispositivos.getInstance().getImpresora1();
		if (printer instanceof IFiscalPrinter && !ticket.isEsDevolucion()) {
			try {
				((IFiscalPrinter) printer).sendTicket((TicketVentaAbono) ticket);
			}
			catch (Throwable e) {
				log.error("registrarTicket() - Error mientras registraba en la impresora fiscal: " + e.getMessage(), e);
				throw new TicketsServiceException(I18N.getTexto("Error mientras se imprimía el ticket en la impresora fiscal."), e);
			}
		}
		
		/*
		 * BYL-179 GENERAR FICHERO .ZIP PARA EDICOM
		 */
		try {
			procesarDocumentoFiscal(ticket);
		} catch (Exception e) {
			if (e instanceof SocketTimeoutException) {
				log.warn("registrarTicket() - Se ha producido un timeout en el envio a Edicom. Se volvera a intentar su envio desde el Backoffice: " + e.getMessage(), e);
			}
			else if (e instanceof ProcesadorIdFiscalException) {
				log.error("registrarTicket() - Error mientras se realizaba el proceso fiscal: " + e.getMessage(), e);
				throw new ProcesadorIdFiscalException(e);
			}
			else if (e instanceof ProcesarDocumentoFiscalPAException) {
				log.error("registrarTicket() - Error mientras se realizaba el proceso fiscal: " + e.getMessage(), e);
				throw new ProcesarDocumentoFiscalPAException(e);

			}
			else {
				log.error("registrarTicket() - Error mientras se realizaba el proceso fiscal: " + e.getMessage(), e);
				throw new TicketsServiceException(e);
			}
		}

		// ENVÍO MENSAJES ERP
		rellenarDatosEnvioERP(ticket);
		
		reiniciarContadoresLineas(ticket);

		IPagoTicket cambio = ticket.getTotales().getCambio();
		List<PagoTicket> pagos = ((TicketVenta) ticket).getPagos();

		// Borramos pagos a cero
		ListIterator<PagoTicket> listIterator = pagos.listIterator();
		while (listIterator.hasNext()) {
			PagoTicket pago = listIterator.next();
			if (BigDecimalUtil.isIgualACero(pago.getImporte())) {
				listIterator.remove();
			}
		}
		// Añadimos un pago a cero si el importe total es cero y no hay pagos
		if (BigDecimalUtil.isIgualACero(ticket.getCabecera().getTotales().getTotal()) && pagos.size() == 0) {
			PagoTicket pagoVacio = createPago();
			pagoVacio.setMedioPago(MediosPagosService.medioPagoDefecto);
			pagoVacio.setImporte(BigDecimal.ZERO);
			((TicketVenta) ticket).addPago(pagoVacio);
		}

		// Generamos movimientos de caja
		registrarMovimientosCaja((TicketVentaAbono) ticket, cambio, pagos, sqlSession);

		// Añadimos el cambio como un pago
		if (!BigDecimalUtil.isIgualACero(ticket.getCabecera().getTotales().getCambio().getImporte())) {
			IPagoTicket pagoCodMedPagoCambio = ((TicketVenta) ticket).getPago(cambio.getMedioPago().getCodMedioPago());
			MedioPagoBean medioPagoCambio = ticket.getCabecera().getTotales().getCambio().getMedioPago();

			if (pagoCodMedPagoCambio == null) {
				pagoCodMedPagoCambio = createPago();
				pagoCodMedPagoCambio.setEliminable(false);
				pagoCodMedPagoCambio.setModificable(true);
				pagoCodMedPagoCambio.setMedioPago(medioPagoCambio);

				((TicketVenta) ticket).addPago(pagoCodMedPagoCambio);
			}

			pagoCodMedPagoCambio.setImporte(pagoCodMedPagoCambio.getImporte().subtract(((TicketVenta) ticket).getTotales().getCambio().getImporte()));
		}

		// Borramos pagos a cero
		listIterator = pagos.listIterator();
		while (listIterator.hasNext()) {
			PagoTicket pago = listIterator.next();
			if (BigDecimalUtil.isIgualACero(pago.getImporte())) {
				listIterator.remove();
			}
		}
		// Añadimos un pago a cero si el importe total es cero y no hay pagos
		if (BigDecimalUtil.isIgualACero(ticket.getCabecera().getTotales().getTotal()) && pagos.size() == 0) {
			PagoTicket pagoVacio = createPago();
			pagoVacio.setMedioPago(MediosPagosService.medioPagoDefecto);
			pagoVacio.setImporte(BigDecimal.ZERO);
			((TicketVenta) ticket).addPago(pagoVacio);
		}

		String firma = generarFirma(sqlSession, ticket);

		log.debug("registrarTicket() - Ejecutando listeners posteriores al guardado del ticket");
		listenersExecutor.executeListeners(SalvadoTicketListener.class, "executeBeforeSave", sqlSession, ticket, tipoDocumento);

		// Construimos objeto persistente
		log.debug("registrarTicket() - Construyendo objeto persistente...");
		ticketBean = new TicketBean();
		ticketBean.setCodAlmacen(ticket.getCabecera().getTienda().getAlmacenBean().getCodAlmacen());
		ticketBean.setCodcaja(ticket.getCodCaja());
		ticketBean.setFecha(ticket.getFecha());
		ticketBean.setIdTicket(ticket.getIdTicket());
		ticketBean.setUidTicket(ticket.getUidTicket());
		ticketBean.setIdTipoDocumento(ticket.getCabecera().getTipoDocumento());
		ticketBean.setCodTicket(ticket.getCabecera().getCodTicket());
		ticketBean.setSerieTicket(ticket.getCabecera().getSerieTicket());
		ticketBean.setFirma(firma);

		String hashControl = ticket.getCabecera().getFirma().getHashControl();
		FirmaTicket firmaTicket = new FirmaTicket();
		firmaTicket.setHashControl(hashControl);
		firmaTicket.setFirma(ticketBean.getFirma());
		ticket.getCabecera().setFirma(firmaTicket);

		/*
		 * Comprobamos los datos de la cabecera del Ticket, y seguidamente le setamos el CodCanal para que aparezca como
		 * tag de la cabecera del Ticket.
		 */
		if (ticket.getCabecera().getTienda() != null) {
			if (ticket.getCabecera().getTienda().getTiendaBean() != null) {
				if (StringUtils.isNotBlank(ticket.getCabecera().getTienda().getTiendaBean().getCodcanal())) {
					String codCanal = ticket.getCabecera().getTienda().getTiendaBean().getCodcanal();
					((ByLCabeceraTicket) ticket.getCabecera()).setCodCanal(codCanal);
				}
			}
		}
		
		setFiscalData(ticket);
		List<LineaTicket> lineasTicket = ticket.getLineas();
		
		if(((ByLCabeceraTicket) ticket.getCabecera()).getCabeceraVertex() != null) {
			vertexService.setDocumentNumber(ticket);
			((ByLCabeceraTicket) ticket.getCabecera()).getCabeceraVertex().setAmount(ticket.getTotales().getBase().toString());
			((ByLCabeceraTicket) ticket.getCabecera()).getCabeceraVertex().setAmountWithTax(ticket.getTotales().getTotal().toString());
		}
		
		if (ticket.getCabecera().getDatosDocOrigen() != null && ticket.getCabecera().getDatosDocOrigen().getFecha() != null) {
			Date fechaOrigen = FormatUtil.getInstance().desformateaFechaHoraTicket(ticket.getCabecera().getDatosDocOrigen().getFecha());
			if (fechaOrigen == null) {
				try {
					SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm");
					fechaOrigen = formato.parse(ticket.getCabecera().getDatosDocOrigen().getFecha());
				}
				catch (Exception e) {
					SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
					try {
						fechaOrigen = formato.parse(ticket.getCabecera().getDatosDocOrigen().getFecha());
					}
					catch (ParseException e1) {
						log.warn("registrarTicket() - No se ha podido parsear la fecha origen: " + ticket.getCabecera().getDatosDocOrigen().getFecha());
					}
				}

			}
			
			/* Se aniade comprobacion ya que habra paises que no cumplan con el formato anterior */
			/* Este campo solo se usara Panama y Ecuador */
			if (fechaOrigen != null) {
				((ByLCabeceraTicket) ticket.getCabecera()).setFechaDocumentoOrigen(FormatUtil.getInstance().formateaFechaHoraTicket(fechaOrigen));
			}
		}
		else if (fechaOrigenSinTicketReferenciar != null) {
			((ByLCabeceraTicket) ticket.getCabecera()).setFechaDocumentoOrigen(FormatUtil.getInstance().formateaFechaHoraTicket(fechaOrigenSinTicketReferenciar));
		}
		
		if (lineasTicket != null) {
			for (LineaTicket lineaTicket : lineasTicket) {
				if (lineaTicket instanceof ByLLineaTicketProfesional) {
					/* [BYL-133] Introducimos el totalImpuestos, y los porcentajes en las lineas */
					BigDecimal totalImpuestos = ((LineaTicketProfesional) lineaTicket).getTotalIva().add(((LineaTicketProfesional) lineaTicket).getTotalRecargo());
					((ByLLineaTicketProfesional) lineaTicket).setTotalImpuestos(totalImpuestos);
					BigDecimal porcentaje = ((SubtotalIvaTicket) ticket.getCabecera().getSubtotalesIva().get(0)).getPorcentaje();
					((ByLLineaTicketProfesional) lineaTicket).setPorcentaje(porcentaje);
					BigDecimal porcentajeRecargo = ((SubtotalIvaTicket) ticket.getCabecera().getSubtotalesIva().get(0)).getPorcentajeRecargo();
					((ByLLineaTicketProfesional) lineaTicket).setPorcentajeRecargo(porcentajeRecargo);
				}
			}
		}
		
		if("EC".equals(tipoDocumento.getCodpais())) {
			PropiedadDocumentoBean propiedadCrearClaveAccesoWebPOS = documentos.getDocumento(ticket.getCabecera().getTipoDocumento()).getPropiedades().get(CREAR_CLAVE_ACCESO_WEBPOS);
			if(propiedadCrearClaveAccesoWebPOS != null && "S".equals(propiedadCrearClaveAccesoWebPOS.getValor())) {
				((ByLCabeceraTicket) ticket.getCabecera()).setClaveAcceso(crearClaveDeAccesoWebPOS(ticket));
			}
		}
		
		Integer turnoActual = cajasService.countTurnosCaja(sesion.getSesionCaja().getCajaAbierta());
		((ByLCabeceraTicket) ticket.getCabecera()).setTurno(turnoActual);
		
		
		// BYL-302 - Mensaje cierre de caja
		tipoOperacionVentaService.insertarTipoOperacionVenta(ticket);
		
		// BYL-335
		try {
			String moneda = null;
			PaisBean pais = paisService.consultarCodPais(sesion.getAplicacion().getTienda().getCliente().getCodpais());
			if (pais != null && StringUtils.isNotBlank(pais.getCodDivisa())) {
				moneda = pais.getCodDivisa();
			}
			((ByLCabeceraTicket) ticket.getCabecera()).setMoneda(moneda);
		}
		catch (PaisNotFoundException | PaisServiceException e) {
			log.error("registrarTicket() - No se ha podido consultar la moneda del país durante el registro del ticket: " + e.getMessage(), e);
		}
		//
		
		log.debug("Generando XML del ticket...");
		xmlTicket = MarshallUtil.crearXML(ticket);
		ticketBean.setTicket(xmlTicket);

		log.debug("TICKET: " + ticket.getUidTicket() + "\n" + new String(xmlTicket, "UTF-8") + "\n");

		insertarTicket(sqlSession, ticketBean, false);

		/***
		 * PERSONALIZACIÓN BIMBA Y LOLA - Asignación de contador
		 */
		byLServicioContadores.salvarContador(sqlSession, ((ByLCabeceraTicket) ticket.getCabecera()).getContador());
		/***
		 * FIN PERSONALIZACIÓN
		 */
			
		log.debug("registrarTicket() - Ejecutando listeners posteriores al guardado del ticket");
		listenersExecutor.executeListeners(SalvadoTicketListener.class, "executeAfterSave", sqlSession, ticket, tipoDocumento, ticketBean);

		log.debug("registrarTicket() - Eliminando copia de seguridad...");
		copiaSeguridadTicketService.eliminarBackup(sqlSession, ticketBean.getUidTicket());

		if (ticketBean != null && xmlTicket != null && procesarTicket) {
			try {
				log.debug("registrarTicket() - Procesando ticket...");
				procesarTicket(ticketBean, xmlTicket);
			}
			catch (Exception e) {
				log.warn("registrarTicket() - Ha ocurrido un error procesando ticket: " + e.getMessage(), e);
			}
		}

		try {
			log.debug("registrarTicket() - Ejecutando listeners posteriores al commit del ticket");
			listenersExecutor.executeListeners(SalvadoTicketListener.class, "executeAfterCommit", sqlSession, ticket, tipoDocumento);
		}
		catch (Exception e) {
			throw new TicketsServiceException(e);
		}
	}
	
	@SuppressWarnings("rawtypes")
	private void rellenarDatosEnvioERP(Ticket ticket) throws DocumentoException {
		// [BYL-294] - Tipo y signo de los documentos
		documentos.setPropiedadesDocumento(ticket);

		if (ticket.getDatosFacturacion() != null) {
			((ByLCabeceraTicket) ticket.getCabecera()).setTipoIdFiscalCliente(ticket.getDatosFacturacion().getTipoIdentificacion());
		}
	}

	@SuppressWarnings("rawtypes")
	public void asignarIdTransaccion(Ticket ticket) throws TicketsServiceException {
		try {
            log.debug("consultarContadorTransaccion() - Obteniendo contador para transacción...");            
            
            ContadorBean idTransaccionContador = servicioContadores.obtenerContador(CONTADOR_TRANSACCION, ticket.getUidActividad());
            		
            String idTransaccion = String.valueOf(idTransaccionContador.getValor());
      	           
        	((ByLCabeceraTicket)ticket.getCabecera()).setIdTransaccion(idTransaccion);

    	}
        catch (Exception e) {
            String msg = "Se ha producido un error consultando el contador para la transacción del ticket: " + ticket.getUidTicket() + " : " + e.getMessage();
            log.error("asignarIdTransaccion() - " + msg, e);
            throw new TicketsServiceException(e);
        }
	}
	

	@SuppressWarnings("rawtypes")
	public boolean accionTaxFree(ITicket ticketOperacion) throws Exception {
		SqlSession sqlSession = SpringContext.getBean(SpringTransactionSqlSession.class);

		String rutaCarpeta = variablesService.getVariableAsString(TaxFreeLeerXML.TAXFREE_RUTA_CARPETA);
		String rutaEjecutable = rutaCarpeta + "\\" + TaxFreeLeerXML.NOMBRE_EJECUTABLE;
		String rutaFicheroXML = rutaCarpeta + "\\" + TaxFreeLeerXML.NOMBRE_CARPETA_FICHEROS + "\\";
		String nombreFichero = rutaFicheroXML + ticketOperacion.getUidTicket() + ".xml";

		TaxFreeLeerXML.leerXML((ByLTicketVentaAbono) ticketOperacion, nombreFichero);
		TaxFreeProcesador p = new TaxFreeProcesador(rutaCarpeta, rutaEjecutable);
		String respuesta = p.run("-FileName:" + ticketOperacion.getUidTicket());

		// Una vez realizado el proceso, borraremos el .xml
		File file = new File(nombreFichero);
		file.delete();

		if (respuesta != null) {
			// Si todo ha ido OK, el programa nos dejará el voucher/xml con informfación (num formulario/num pasaporte/
			// etc)

			// Primero leeremos el xml

			TaxFreeXMLVoucher voucher = leerXMLVoucher(respuesta, ticketOperacion);
			byte[] xmlVoucher = MarshallUtil.crearXML(voucher);

			// Creamos el documento TAXFREE
			// Primero obtenemos el contador

			Map<String, String> parametrosContador = new HashMap<>();

			parametrosContador.put("CODEMP", ((Ticket) ticketOperacion).getEmpresa().getCodEmpresa());
			parametrosContador.put("CODALM", ((Ticket) ticketOperacion).getTienda().getAlmacenBean().getCodAlmacen());
			parametrosContador.put("CODSERIE", ((Ticket) ticketOperacion).getTienda().getAlmacenBean().getCodAlmacen());
			parametrosContador.put("CODCAJA", ((Ticket) ticketOperacion).getCodCaja());
			parametrosContador.put("CODTIPODOCUMENTO", "TF"); // Codigo Tipo Documento TAXFREE
			parametrosContador.put("PERIODO", ((new Fecha()).getAño().toString()));

			TipoDocumentoBean documentoActivo = sesion.getAplicacion().getDocumentos().getDocumento("TF");
			ContadorBean ticketContador = byLServicioContadores.obtenerContador(documentoActivo.getIdContador(), parametrosContador, ((Ticket) ticketOperacion).getUidActividad());

			TicketBean ticketBean = new TicketBean();
			ticketBean.setCodAlmacen(((Ticket) ticketOperacion).getCabecera().getTienda().getAlmacenBean().getCodAlmacen());
			ticketBean.setCodcaja(((Ticket) ticketOperacion).getCodCaja());
			ticketBean.setFecha(new Date());
			ticketBean.setIdTicket(ticketContador.getValor());
			ticketBean.setUidTicket(UUID.randomUUID().toString());
			ticketBean.setIdTipoDocumento(documentoActivo.getIdTipoDocumento()); // Id Tipo Documento TAXFREE
			ticketBean.setCodTicket(ticketContador.getValorFormateado());
			ticketBean.setSerieTicket(ticketContador.getConfigContador().getValorDivisor3());

			ticketBean.setTicket(xmlVoucher);
			ticketBean.setFirma("*");

			insertarTicket(sqlSession, ticketBean, false);

			return true;

		}
		else {
			return false;
		}
	}

	@SuppressWarnings("rawtypes")
	private TaxFreeXMLVoucher leerXMLVoucher(String nombreFichero, ITicket ticketOperacion) {
		TaxFreeXMLVoucher voucher = new TaxFreeXMLVoucher();

		voucher.setNumeroFormulario(nombreFichero.replace("-", ""));

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String fecha = sdf.format(new Date());
		voucher.setFecha(fecha);

		voucher.setUidActividad(((ByLTicketVentaAbono) ticketOperacion).getUidActividad());
		voucher.setUidTicket(((ByLTicketVentaAbono) ticketOperacion).getUidTicket());

		return voucher;
	}
	
	public List<ByLTicketBean> consultarTicketsParaHistorico(String caja, Long idTicket, Date fecha, Long idDoc, List<Long> idTiposDocValidos) throws TicketsServiceException {
		try {
			log.debug("consultarTicketsParaHistorico() - Consultando ticket en base de datos...");
			ByLTicketExample example = new ByLTicketExample();
			example.setOrderByClause("tick." + TicketExample.ORDER_BY_FECHA);

			ByLTicketExample.Criteria criteria = example.createCriteria();
			criteria.customAndUidActividadEqualTo(sesion.getAplicacion().getUidActividad());
			if (idDoc != null) {
				criteria.customAndIdTipoDocumentoEqualTo(idDoc);
			}
			if (caja != null && !caja.isEmpty()) {
				criteria.customAndCodcajaEqualTo(caja);
			}
			if (idTicket != null) {
				criteria.customAndIdTicketEqualTo(idTicket);
			}
			if (fecha != null) {
				// Se crea la fecha del día siguiente para crear el intervalo válido para el día del filtro
				Fecha diaSuperior = Fecha.getFecha(fecha);
				diaSuperior.sumaDias(1);
				criteria.customAndFechaBetween(fecha, diaSuperior.getDate());
			}
			if (idTiposDocValidos != null && !idTiposDocValidos.isEmpty()) {
				criteria.customAndIdTipoDocumentoIn(idTiposDocValidos);
			}
			
			/* Debido a una incidencia en la que el idTicket y la firma puede esta null, añadimos este criteria */
			criteria.andIdTicketIsNotNull();

			// example.or(criteria);
			long consultaHistorico = System.currentTimeMillis();
			List<ByLTicketBean> res = ByLTicketMapper.selectForHistoricalSearch(example);
			
			long consultaFinalHistorico = System.currentTimeMillis();
			log.debug("consultarTicketsParaHistorico() - En consultar el histórico ha tardado: " + (consultaFinalHistorico - consultaHistorico) + " msg, se ha obtenido un total de "+res.size()+ " resultados");
			
			return res;
		}
		catch (Exception e) {
			String msg = "Se ha producido un error consultando tickets en base de datos con parámetros indicados: Fecha: " + fecha + " IdTicket: " + idTicket + " Caja: " + caja + " - "
			        + e.getMessage();
			log.error("consultarTicketsParaHistorico() - " + msg, e);
			throw new TicketsServiceException(e);
		}
	}
	
	@Override
	public synchronized void insertarTicket(SqlSession sqlSession, TicketBean ticket, boolean ticketProcesado) throws TicketsServiceException {
		try {
			log.debug("insertarTicket() - Salvando ticket en base de datos...");
			ticket.setUidActividad(sesion.getAplicacion().getUidActividad());

			/*
			 * Puede darse el caso de que se generen tickets a 0 pero sin idTicket ni firma, en ese caso, para que el
			 * procesador de tickets no haga nada, se actualizará el ticket con PROCESADO = S y con un mensaje de error
			 */

			if (StringUtils.isBlank(ticket.getFirma()) && ticket.getIdTicket() == null) {
				log.debug("insertarTicket() - El ticket se ha generado con la firma y el idTicket a null");
				ticket.setProcesado(true);
				ticket.setMensajeProceso(I18N.getTexto("Se ha generado un ticket erróneo debido a un control del POS."));
			}
			else {

				ticket.setProcesado(ticketProcesado);
			}
			ticketMapper.insert(ticket);
		}
		catch (Exception e) {
			String msg = "Se ha producido un error insertando ticket con uid " + ticket.getUidTicket() + " : " + e.getMessage();
			log.error("insertarTicket() - " + msg, e);
			throw new TicketsServiceException(e);
		}
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	protected void setFiscalData(ITicket ticket) throws FiscalDataException {
		try {
			FiscalDataService fiscalService = (FiscalDataService) SpringContext.getBean("ByLFiscalDataService"+sesion.getAplicacion().getTienda().getCliente().getCodpais());
			FiscalData fiscalData = fiscalService.getFiscalData(ticket);
			ticket.getCabecera().setFiscalData(fiscalData);
		} catch (NoSuchBeanDefinitionException e) {
			try {
				log.debug("setFiscalData() - No está configurado el servicio fiscal personalizado ByLFiscalDataService" + sesion.getAplicacion().getTienda().getCliente().getCodpais());
				FiscalDataService fiscalService = (FiscalDataService) SpringContext.getBean("FiscalDataService"+sesion.getAplicacion().getTienda().getCliente().getCodpais());
				FiscalData fiscalData = fiscalService.getFiscalData(ticket);
				ticket.getCabecera().setFiscalData(fiscalData);
			} catch (NoSuchBeanDefinitionException e2) {
				log.debug("setFiscalData() - No hay configurado un servicio fiscal para el país '"+sesion.getAplicacion().getTienda().getCliente().getCodpais()+"'");
			}	
		}		
		
	}
	
	/* BYL-159 Envío email finalización del contador */
	@SuppressWarnings("rawtypes")
	public void enviarCorreoRango(ITicket ticketOperacion) throws Exception {
		SqlSession sqlSession = SpringContext.getBean(SpringTransactionSqlSession.class);
		
		Map<String, String> parametrosContador = new HashMap<>();
		parametrosContador.put("CODTIPODOCUMENTO", "NOTIFCLI"); // Código Tipo Documento NOTIFICACION CLIENTE
		
		LinkedHashMap<String, String> parametrosCorreo = new LinkedHashMap<String, String>();
		parametrosCorreo.put("codAlm", ((Ticket) ticketOperacion).getTienda().getAlmacenBean().getCodAlmacen());

		TipoDocumentoBean documentoActivo = sesion.getAplicacion().getDocumentos().getDocumento("NOTIFCLI");
		ContadorBean ticketContador = byLServicioContadores.obtenerContador(documentoActivo.getIdContador(), parametrosContador, ((Ticket) ticketOperacion).getUidActividad());
		String emailEnvio = variablesService.getVariableAsString(VARIABLE_CORREO_ENVIO_FIN_CONTADOR);
		
		ContactoModel contacto = new ContactoModel();
		contacto.setClaveContacto("EMAIL");
		contacto.setIdCanal(1L);
		contacto.setIdPlantillaMensaje(110L);
		contacto.setValorContacto(emailEnvio);
		
		DestinatarioModel model = new DestinatarioModel();
		model.setContactos(Arrays.asList(contacto));
		model.setParametros(parametrosCorreo);
		
		NotificacionModel notification = new NotificacionModel();
		notification.setCodalm(((Ticket) ticketOperacion).getTienda().getAlmacenBean().getCodAlmacen());
		notification.setCodemp(((Ticket) ticketOperacion).getEmpresa().getCodEmpresa());
		notification.setTipoNotificacion(NOTIFICACION_CONTADOR);
		notification.setUidActividad(((Ticket) ticketOperacion).getUidActividad());
		notification.setDestinatarios(Arrays.asList(model));
		
		byte[] xml = MarshallUtil.crearXML(notification);
		
		TicketBean ticketBean = new TicketBean();
		ticketBean.setCodAlmacen(((Ticket) ticketOperacion).getCabecera().getTienda().getAlmacenBean().getCodAlmacen());
		ticketBean.setCodcaja(((Ticket) ticketOperacion).getCodCaja());
		ticketBean.setFecha(new Date());
		ticketBean.setIdTicket(ticketContador.getValor());
		ticketBean.setUidTicket(UUID.randomUUID().toString());
		ticketBean.setIdTipoDocumento(documentoActivo.getIdTipoDocumento()); // Id Tipo Documento TAXFREE
		ticketBean.setCodTicket(ticketContador.getValorFormateado());
		ticketBean.setSerieTicket(ticketContador.getConfigContador().getValorDivisor3());

		ticketBean.setTicket(xml);
		ticketBean.setFirma("*");

		insertarTicket(sqlSession, ticketBean, false);		
	}

	@SuppressWarnings("rawtypes")
	private void procesarDocumentoFiscal(Ticket ticket) throws Exception {
		log.debug("procesarDocumentoFiscal()");
		PropiedadDocumentoBean propiedadClaseProcesamiento = documentos.getDocumento(ticket.getCabecera().getTipoDocumento()).getPropiedades().get(CLASE_PROCESAMIENTO_IDFISCAL);

		if (propiedadClaseProcesamiento != null && !propiedadClaseProcesamiento.getValor().isEmpty()) {
			IProcesadorFiscal procesadorIdFiscal;
			Class<?> clazz = null;

			clazz = Class.forName(propiedadClaseProcesamiento.getValor());

			try {
				procesadorIdFiscal =  (IProcesadorFiscal) context.getBean(clazz);
			}
			catch (Exception e) {
				procesadorIdFiscal = (IProcesadorFiscal) clazz.newInstance();
			}
			
			procesadorIdFiscal.procesarDocumentoFiscal(ticket);
		}
	}
	
	public TicketBean consultarTicketAbonobyCodTicket(String codTienda, String codCaja, String codTicket, Long idTipoDoc) throws TicketsServiceException {
		SqlSession sqlSession = new SqlSession();
		TicketBean res = null;
		try {
			log.debug("consultarTicketAbonobyCodTicket() - Consultando ticket en base de datos...");
			sqlSession.openSession(SessionFactory.openSession());
			TicketExample example = new TicketExample();
			TicketExample.Criteria criteria = example.createCriteria();
			criteria.andUidActividadEqualTo(sesion.getAplicacion().getUidActividad()).andCodAlmacenEqualTo(codTienda).andCodTicketEqualTo(codTicket).andCodcajaEqualTo(codCaja)
			        .andIdTipoDocumentoEqualTo(idTipoDoc);
			example.setOrderByClause("FECHA DESC");
			List<TicketBean> resultados = ticketMapper.selectByExampleWithBLOBs(example);
			if (resultados != null && !resultados.isEmpty()) {
				res = resultados.get(0);
			}
			return res;
		}
		catch (Exception e) {
			String msg = "Se ha producido un error consultando tickets en base de datos con parámetros indicados: idTienda: " + codTienda + ", idTicket: " + codTicket;
			log.error("consultarTicketAbonobyCodTicket() - " + msg, e);
			throw new TicketsServiceException(e);
		}
		finally {
			sqlSession.close();
		}
	}

	@SuppressWarnings("rawtypes")
	/**
	 * 
	 * @param ticket
	 * @return clave de acceso WEBPOS
	 * @throws TicketsServiceException
	 * @throws ValidadorDocumentoIdentificacionException
	 */
	public String crearClaveDeAccesoWebPOS(Ticket ticket) throws TicketsServiceException, ValidadorDocumentoIdentificacionException {
		/*
		 * <AccessCode>clave</AccessCode>
		 * 16012024 01 1101160032 1 003 010 000000002 12345678 1 0
		 * 16012024 01 5278232 1 003 010 000000001 12345678 1 0
		 * EJEMPLO CLAVE --> 26042019-01-1792082935001-2-001-011-000020847-12345678-1-6 
		 * - Fecha -> 26/04/2019 
		 * - CodTipoDoc -> BO->01 / NC->04 
		 * - RUC Ecuador -> 1792082935001 (Cédula + 001) 
		 * - Ambiente de facturación electrónica: 1 es Pruebas y 2 es Producción 
		 * - BranchCod -> 001 
		 * - CodPOS -> 011 
		 * - DocNumber -> 000020847 
		 * - Código constante -> 12345678 
		 * - Constante -> 1
		 * - DV -> 6 (Dígito que se calcula a través del resto de la clave)
		 */

		log.debug("crearClaveDeAccesoWebPOS() - Creando clave de acceso para WEBPOS");

		// Fecha
		SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
		String fecha = dateFormat.format(ticket.getFecha());

		// CodTipoDoc
		String codTipoDoc = "BO".equals(ticket.getCabecera().getCodTipoDocumento()) ? "01" : "04";

		// Ruc
		String ruc = sesion.getAplicacion().getEmpresa().getCif();

		// Ambiente de facturación electrónica
		String variableAmbiente = variablesService.getVariableAsString(ByLVariablesServices.EC_WEBPOS_ENTORNO);
		String ambiente = (variableAmbiente == null || variableAmbiente.isEmpty()) ? "1" : variableAmbiente;
		

		// BranchCod (3)
		String branchCod = FormatUtil.getInstance()
		        .completarCerosIzquierda(ticket.getTienda().getCodAlmacen().substring(ticket.getTienda().getCodAlmacen().length() - 2, ticket.getTienda().getCodAlmacen().length()), 3);

		// CodPOS (3)
		String codPOS = FormatUtil.getInstance().completarCerosIzquierda(ticket.getCodCaja().substring(ticket.getCodCaja().length() - 2, ticket.getCodCaja().length()), 3);

		// Secuencial (9)
		String secuencial = FormatUtil.getInstance().completarCerosIzquierda(((ByLCabeceraTicket) ticket.getCabecera()).getIdentificadorFiscal(), 9);

		// DocNumber (BranchCod + CodPOS + Secuencial)
		String docNumber = branchCod + codPOS + secuencial;

		// Creamos la clave final a partir de todos los datos recogidos del ticket
		String clave = fecha + codTipoDoc + ruc + ambiente + docNumber + "12345678" + "1";

		byte dv = getDV(clave);
		clave = clave + dv;

		log.debug("crearClaveDeAccesoWebPOS() - " + fecha + "-" + codTipoDoc + "-" + ruc + "-" + ambiente + "-" + docNumber + "-" + "12345678" + "-" + "1" + "-" + dv);
		log.debug("crearClaveDeAccesoWebPOS() - La clave que se ha generado es: " + clave + " (longitud: " + clave.length() + ")");

		if (clave.length() != 49) {
			throw new TicketsServiceException("Error al generar la clave de acceso para WEBPOS (" + clave + ")");
		}
		return clave;
	}
	
	/**
	 * Método proporcionado por el cliente para obtener el DV a partir de la clave de acceso para tenerla al completo
	 * @param tNumericCode
	 * @return DV
	 */
	public static byte getDV(String tNumericCode) {
		log.debug("getDV() - Obteniendo el dígito DV para la clave: " + tNumericCode);
		try {
			int f = 2;
			int r = 0;
			int m = 0;

			for (int i = 47; i >= 0; i--) {
				if (f > 7) {
					f = 2;
				}
				r += Integer.parseInt(tNumericCode.substring(i, i + 1)) * f;
				f++;
			}

			m = r % 11;
			m = (11 - m);

			if (m == 10) {
				m = 1;
			}
			if (m == 11) {
				m = 0;
			}
			log.debug("getDV() - El dígito DV es: " + m);
			return (byte) m;
		}
		catch (Exception e) {
			log.error("getDV() - Error al calcular el DV de la clave de acceso de WEBPOS: ", e);
			return 0;
		}
	}
	
	@Override
	protected void registrarMovimientosCaja(TicketVentaAbono ticket, IPagoTicket cambio, List<PagoTicket> pagos, SqlSession sqlSession) throws CajasServiceException {
		log.debug("registrarTicket() - Registramos movimientos de caja...");
		Integer idLineaCaja = cajasService.consultarProximaLineaDetalleCaja(sqlSession);
		//Registramos entregas a cuenta
		if (ticket.getEntregasCuenta() != null && ticket.getEntregasCuenta().getEntregasCuenta() != null) {
			for (EntregaCuentaTicket entregaCuentaTicket : ticket.getEntregasCuenta().getEntregasCuenta()) {
				CajaMovimientoBean detalleCaja = new CajaMovimientoBean();
				detalleCaja.setLinea(idLineaCaja);
			    detalleCaja.setFecha(ticket.getFecha());
			    detalleCaja.setConcepto(ticket.getCabecera().getDesTipoDocumento() + ": " + ticket.getCabecera().getCodTicket());
			    detalleCaja.setDocumento(ticket.getCabecera().getCodTicket());
			    detalleCaja.setCodMedioPago(entregaCuentaTicket.getCodMedioPago());
			    detalleCaja.setIdDocumento(ticket.getUidTicket());
			    detalleCaja.setIdTipoDocumento(ticket.getCabecera().getTipoDocumento());
			    detalleCaja.setAbono(BigDecimal.ZERO);
			    detalleCaja.setCargo(entregaCuentaTicket.getImporte());
			    cajasService.crearMovimiento(sqlSession, detalleCaja);
			    idLineaCaja++;
			}
		}
		boolean esVenta =ticket.getCabecera().esVenta();
		
		List<CajaMovimientoTarjeta> movimientosCajaTarjeta = new ArrayList<>();
		BigDecimal totalBaseProporcionalPagos = BigDecimal.ZERO;
		BigDecimal totalImpuestosProporcionalPagos = BigDecimal.ZERO;
		
		//Registramos pagos
		for (IPagoTicket pago : pagos) {
			CajaMovimientoBean detalleCaja = new CajaMovimientoBean();
			detalleCaja.setLinea(idLineaCaja);
			detalleCaja.setFecha(ticket.getFecha());

			if (!esVenta) {
				if (BigDecimalUtil.isMayorOrIgualACero(ticket.getCabecera().getTotales().getTotal())) {
					if (pago.getImporte().compareTo(BigDecimal.ZERO) >= 0) {
						detalleCaja.setCargo(BigDecimal.ZERO);
						detalleCaja.setAbono(pago.getImporte().abs());
					}
					else {
						detalleCaja.setCargo(pago.getImporte().abs());
						detalleCaja.setAbono(BigDecimal.ZERO);
					}
				}
				else {
					if (pago.getImporte().compareTo(BigDecimal.ZERO) < 0) {
						detalleCaja.setCargo(BigDecimal.ZERO);
						detalleCaja.setAbono(pago.getImporte().abs());
					}
					else {
						detalleCaja.setCargo(pago.getImporte().abs());
						detalleCaja.setAbono(BigDecimal.ZERO);
					}
				}
			}
			else {
				if (pago.getImporte().compareTo(BigDecimal.ZERO) < 0) {
					detalleCaja.setCargo(BigDecimal.ZERO);
					detalleCaja.setAbono(pago.getImporte().abs());
				}
				else {
					detalleCaja.setCargo(pago.getImporte().abs());
					detalleCaja.setAbono(BigDecimal.ZERO);
				}
			}

			detalleCaja.setConcepto(ticket.getCabecera().getDesTipoDocumento() + ": " + ticket.getCabecera().getCodTicket());
			detalleCaja.setDocumento(ticket.getCabecera().getCodTicket());
			detalleCaja.setCodMedioPago(pago.getCodMedioPago());
			detalleCaja.setIdDocumento(ticket.getUidTicket());
			detalleCaja.setIdTipoDocumento(ticket.getCabecera().getTipoDocumento());
			cajasService.crearMovimiento(sqlSession, detalleCaja);
			
			
			
			try {
				CajaMovimientoTarjeta movimientoTarjeta = new CajaMovimientoTarjeta();
				movimientoTarjeta.setUidActividad(ticket.getUidActividad());
				movimientoTarjeta.setUidDiarioCaja(ticket.getUidDiarioCaja());
				movimientoTarjeta.setLinea(idLineaCaja);
				/* Generamos el XML del objeto de movimiento de Tarjeta */
				DatosRespuestaTarjetaReservaTicket datosRespuesta = new DatosRespuestaTarjetaReservaTicket();
				BeanUtilsBean.getInstance().getConvertUtils().register(false, false, 0);
				
				byte[] result = null;
				if(datosRespuesta != null && pago.getDatosRespuestaPagoTarjeta() != null) {
					BeanUtils.copyProperties(datosRespuesta, pago.getDatosRespuestaPagoTarjeta());
					
					/* Se añade este control para que no falle al realizar el Marshall */
					if (datosRespuesta.getAdicionales() == null) {
						datosRespuesta.setAdicionales(new HashMap<>());
					}
					
					result = MarshallUtil.crearXML(datosRespuesta);
				}
				movimientoTarjeta.setRespuestaTarjeta(result);

				BigDecimal baseProporcional = calcularImporteProporcional(ticket, pago, TIPO_PROPORCIONAL_BASE);
				BigDecimal impuestoProporcional = calcularImporteProporcional(ticket, pago, TIPO_PROPORCIONAL_IMPUESTOS);
				
				if(!ticket.getCabecera().esVenta()) {
					String codConAlm = null;
					try {
						codConAlm = this.documentos.getDocumento(ticket.getCabecera().getTipoDocumento()).getCodconalm();
					}
					catch (DocumentoException e) {
						log.error("registrarMovimientosCaja() - Error al consultar el documento del ticket: " + e.getMessage());
					}
					if(codConAlm != null && codConAlm.equals("9900")) {
						baseProporcional = baseProporcional.negate();
						impuestoProporcional = impuestoProporcional.negate();
					}
				}
				
				totalBaseProporcionalPagos = totalBaseProporcionalPagos.add(baseProporcional);
				totalImpuestosProporcionalPagos = totalImpuestosProporcionalPagos.add(impuestoProporcional);
				
				movimientoTarjeta.setBase(baseProporcional);
				movimientoTarjeta.setImpuestos(impuestoProporcional);
				
				
				
				movimientosCajaTarjeta.add(movimientoTarjeta);
				
			}
			catch (MarshallUtilException | IllegalAccessException | InvocationTargetException e) {
				String mensajeError = "Error al generar el XML de los Datos de Respuesta de una Tarjeta";
				log.error("registrarMovimientosCaja() - " + mensajeError + " - " + e.getMessage());
				throw new CajasServiceException(mensajeError, e);
			}
			idLineaCaja++;
		}
		
		
		
		//Añadimos el movimiento de cambio
		if(!BigDecimalUtil.isIgualACero(ticket.getCabecera().getTotales().getCambio().getImporte())){
		    CajaMovimientoBean detalleCaja = new CajaMovimientoBean();
		    detalleCaja.setLinea(idLineaCaja);
		    detalleCaja.setFecha(ticket.getFecha());
		    //Cargo o abono al revés de lo normal
		    if(esVenta){
		    	detalleCaja.setCargo(cambio.getImporte().abs().negate());
		        detalleCaja.setAbono(BigDecimal.ZERO);  
		    }else{
		    	detalleCaja.setCargo(BigDecimal.ZERO);
		        detalleCaja.setAbono(cambio.getImporte().abs().negate());
		    }
		    
		    detalleCaja.setConcepto(ticket.getCabecera().getDesTipoDocumento() + ": " + ticket.getCabecera().getCodTicket() + " (cambio)");
		    detalleCaja.setDocumento(ticket.getCabecera().getCodTicket()); 
		    detalleCaja.setCodMedioPago(cambio.getCodMedioPago());
		    detalleCaja.setIdDocumento(ticket.getUidTicket());
		    detalleCaja.setIdTipoDocumento(ticket.getCabecera().getTipoDocumento());
		    cajasService.crearMovimiento(sqlSession, detalleCaja);
		    
		    CajaMovimientoTarjeta movimientoTarjeta = new CajaMovimientoTarjeta();
			movimientoTarjeta.setUidActividad(ticket.getUidActividad());
			movimientoTarjeta.setUidDiarioCaja(ticket.getUidDiarioCaja());
			movimientoTarjeta.setLinea(idLineaCaja);
			/* Generamos el XML del objeto de movimiento de Tarjeta */
			DatosRespuestaTarjetaReservaTicket datosRespuesta = new DatosRespuestaTarjetaReservaTicket();
			BeanUtilsBean.getInstance().getConvertUtils().register(false, false, 0);
			
			byte[] result = null;
			movimientoTarjeta.setRespuestaTarjeta(result);

			BigDecimal baseProporcional = calcularImporteProporcionalCambio(ticket, cambio, TIPO_PROPORCIONAL_BASE);
			BigDecimal impuestoProporcional = calcularImporteProporcionalCambio(ticket, cambio, TIPO_PROPORCIONAL_IMPUESTOS);
			
			if(!ticket.getCabecera().esVenta()) {
				String codConAlm = null;
				try {
					codConAlm = this.documentos.getDocumento(ticket.getCabecera().getTipoDocumento()).getCodconalm();
				}
				catch (DocumentoException e) {
					log.error("registrarMovimientosCaja() - Error al consultar el documento del ticket: " + e.getMessage());
				}
				if(codConAlm != null && codConAlm.equals("9900")) {
					baseProporcional = baseProporcional.negate();
					impuestoProporcional = impuestoProporcional.negate();
				}
			}
			
			totalBaseProporcionalPagos = totalBaseProporcionalPagos.add(baseProporcional);
			totalImpuestosProporcionalPagos = totalImpuestosProporcionalPagos.add(impuestoProporcional);
			
			movimientoTarjeta.setBase(baseProporcional);
			movimientoTarjeta.setImpuestos(impuestoProporcional);
			
			
			
			movimientosCajaTarjeta.add(movimientoTarjeta);
			
		    
		    idLineaCaja++;
		}

		comprobarImportesProporcionales(ticket, totalBaseProporcionalPagos, totalImpuestosProporcionalPagos, movimientosCajaTarjeta);
		for (CajaMovimientoTarjeta cajaMovimientoTarjeta : movimientosCajaTarjeta) {
			try {
				movimientoTarjetaService.crear(cajaMovimientoTarjeta, sqlSession);
			}
			catch (CajaMovimientoTarjetaException | CajaMovimientoTarjetaConstraintViolationException e) {
				String mensajeError = "Error al generar registrar el movimiento de la caja";
				log.error("registrarMovimientosCaja() - " + mensajeError + " - " + e.getMessage());
				throw new CajasServiceException(mensajeError, e);
			}
		}

	}

	private BigDecimal calcularImporteProporcional(TicketVentaAbono ticket, IPagoTicket pago, String tipoProporcional) {
		BigDecimal totalTicket = ticket.getCabecera().getTotales().getTotalAPagar();
		BigDecimal importePago = pago.getImporte();
		BigDecimal importeProporcional = BigDecimal.ZERO;
		if (tipoProporcional.equals(TIPO_PROPORCIONAL_BASE)) {
			BigDecimal baseTicket = ticket.getCabecera().getTotales().getBase();
			importeProporcional = importePago.multiply(baseTicket).divide(totalTicket, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
		}
		else {
			BigDecimal impuestosTicket = ticket.getCabecera().getTotales().getImpuestos();
			importeProporcional = importePago.multiply(impuestosTicket).divide(totalTicket, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
		}
		return importeProporcional;
	}

	private BigDecimal calcularImporteProporcionalCambio(TicketVentaAbono ticket, IPagoTicket cambio, String tipoProporcional) {
		BigDecimal totalTicket = ticket.getCabecera().getTotales().getTotalAPagar();
		BigDecimal importePago = cambio.getImporte().abs().negate();
		BigDecimal importeProporcional = BigDecimal.ZERO;
		if (tipoProporcional.equals(TIPO_PROPORCIONAL_BASE)) {
			BigDecimal baseTicket = ticket.getCabecera().getTotales().getBase();
			importeProporcional = importePago.multiply(baseTicket).divide(totalTicket, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
		}
		else {
			BigDecimal impuestosTicket = ticket.getCabecera().getTotales().getImpuestos();
			importeProporcional = importePago.multiply(impuestosTicket).divide(totalTicket, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
		}
		return importeProporcional;
	}
	
	private void comprobarImportesProporcionales(TicketVentaAbono ticket, BigDecimal totalBaseProporcionalPagos, BigDecimal totalImpuestosProporcionalPagos,
	        List<CajaMovimientoTarjeta> movimientosCajaTarjeta) {
		BigDecimal baseTotales = ticket.getCabecera().getTotales().getBase();
		BigDecimal impuestosTotales = ticket.getCabecera().getTotales().getImpuestos();
		if (baseTotales.compareTo(totalBaseProporcionalPagos) != 0) {
			BigDecimal diferenciaBase = baseTotales.subtract(totalBaseProporcionalPagos);
			movimientosCajaTarjeta.get(0).getBase().add(diferenciaBase);
		}
		if (impuestosTotales.compareTo(totalImpuestosProporcionalPagos) != 0) {
			BigDecimal diferenciaImpuestos = impuestosTotales.subtract(totalImpuestosProporcionalPagos);
			movimientosCajaTarjeta.get(0).getImpuestos().add(diferenciaImpuestos);
		}

	}
}
