package com.comerzzia.cardoso.pos.services.pagos.worldline;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.cardoso.pos.persistence.pagos.wordline.RegistroPagoWordline;
import com.comerzzia.cardoso.pos.persistence.pagos.wordline.RegistroPagoWordlineExample;
import com.comerzzia.cardoso.pos.persistence.pagos.wordline.RegistroPagoWordlineMapper;
import com.comerzzia.cardoso.pos.services.pagos.worldline.exceptions.WorldlineInitException;
import com.comerzzia.cardoso.pos.services.pagos.worldline.exceptions.WorldlinePaymentException;
import com.comerzzia.core.util.fechas.Fecha;
import com.comerzzia.pos.services.core.sesion.SesionAplicacion;
import com.comerzzia.pos.services.core.sesion.SesionCaja;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.payments.PaymentDto;
import com.comerzzia.pos.services.payments.PaymentException;
import com.comerzzia.pos.services.payments.methods.types.BasicPaymentMethodManager;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.services.ticket.pagos.tarjeta.DatosRespuestaPagoTarjeta;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.ingenico.fr.jc3api.JC3ApiC3Rspn;
import com.ingenico.fr.jc3api.JC3ApiC3RspnExt;
import com.ingenico.fr.jc3api.JC3ApiConstants.C3Currencies;
import com.ingenico.fr.jc3api.JC3ApiInterface;
import com.ingenico.fr.jc3api.JC3ApiInterfaceNet;
import com.ingenico.fr.jc3api.JC3ApiParams;

@Service
public class WorldlineService {

	private static final Logger log = Logger.getLogger(WorldlineService.class);

	// Operaciones
	private final String PAY = "PAY";
	private final String REFUND = "REFUND";
	private final String CANCEL_PAY = "CANCEL PAY";
	private final String CANCEL_REFUND = "CANCEL REFUND";
	private final String LECTURA_TARJETA = "LECTURA DE TARJETA";
	private final String INIT = "INICIALIZACIÓN";
	private final String INIT_NO_TMS = "INICIALIZACIÓN SIN TMS";

	private final String VARIABLE_BORRAR_REGISTROS_PAGOS_TARJETA = "X_POS.BORRAR_PAGOS_TARJETA";
	private String EUR_CURRENCY = C3Currencies.C3_CURRENCY_EUR.getCurrencyCodeNum();

	@Autowired
	protected SesionAplicacion sesionAplicacion;
	@Autowired
	protected SesionCaja sesionCaja;
	@Autowired
	protected VariablesServices variableService;
	@Autowired
	protected RegistroPagoWordlineMapper mapper;

	private JC3ApiInterface c3Agent = null;
	private C3Callbacks c3Callbacks = null;

	public void salvar(String uidActividad, Date fechaOperacion, String tipoOperacion, BigDecimal importe) {
		RegistroPagoWordlineExample example = new RegistroPagoWordlineExample();
		example.or().andUidActividadEqualTo(uidActividad).andTipoOperacionEqualTo(tipoOperacion).andFechaEqualTo(fechaOperacion);

		List<RegistroPagoWordline> totales = mapper.selectByExample(example);

		if (totales != null && !totales.isEmpty()) {
			RegistroPagoWordline registro = totales.get(0);

			BigDecimal totalActual = registro.getImporte();
			BigDecimal nuevoTotal = totalActual.add(importe);

			registro.setImporte(nuevoTotal);

			if (tipoOperacion.equals(PAY)) {
				registro.setNumOperVenta(registro.getNumOperVenta() + 1);

			}
			else if (tipoOperacion.equals(REFUND)) {
				registro.setNumOperDevol(registro.getNumOperDevol() + 1);
			}
			else {
				registro.setNumOperAnul(registro.getNumOperAnul() + 1);
			}

			mapper.updateByPrimaryKey(registro);
		}
		else {
			RegistroPagoWordline registro = new RegistroPagoWordline();
			registro.setFecha(fechaOperacion);
			registro.setImporte(importe);
			registro.setTipoOperacion(tipoOperacion);
			registro.setUidActividad(uidActividad);

			if (tipoOperacion.equals(PAY)) {
				registro.setNumOperVenta(1);

			}
			else if (tipoOperacion.equals(REFUND)) {
				registro.setNumOperDevol(1);
			}
			else {
				registro.setNumOperAnul(1);
			}

			mapper.insert(registro);
		}

	}

	public ConsultaTotalesBean consutarTotales() {
		ConsultaTotalesBean totales = new ConsultaTotalesBean();

		RegistroPagoWordlineExample example = new RegistroPagoWordlineExample();
		example.or().andUidActividadEqualTo(sesionAplicacion.getUidActividad()).andFechaEqualTo(new Date());

		List<RegistroPagoWordline> registros = mapper.selectByExample(example);

		if (registros != null && !registros.isEmpty()) {
			for (RegistroPagoWordline registro : registros) {
				if (registro.getTipoOperacion().equals(PAY)) {
					totales.setNumVentas(registro.getNumOperVenta());
					totales.setTotalVentas(registro.getImporte());
				}
				else if (registro.getTipoOperacion().equals(REFUND)) {
					totales.setNumDevoluciones(registro.getNumOperDevol());
					totales.setTotalDevoluciones(registro.getImporte());
				}
				else { // CANCEL_PAY y CANCEL_REFUND
					totales.setNumAnulaciones(registro.getNumOperAnul());
					totales.setTotalAnulaciones(registro.getImporte());
				}
			}
		}

		return totales;

	}

	public void borrarRegistroPagosTarjeta() {
		log.debug("borrarRegistroPagosTarjeta() - Comprobando el número de días configurado para el borrado de los registros de los pagos con tarjeta");
		Integer dias = variableService.getVariableAsInteger(VARIABLE_BORRAR_REGISTROS_PAGOS_TARJETA);
		Fecha fechaBorrado = new Fecha(new Date());
		fechaBorrado.sumaDias(-dias);
		log.debug("borrarRegistroPagosTarjeta() - Se van a borrar los registros con fecha anterior a " + fechaBorrado.getString());

		RegistroPagoWordlineExample example = new RegistroPagoWordlineExample();
		example.or().andUidActividadEqualTo(sesionAplicacion.getUidActividad()).andFechaLessThan(fechaBorrado.getDate());

		Integer registrosBorrados = mapper.deleteByExample(example);
		log.debug("borrarRegistroPagosTarjeta() - Se han borrado " + registrosBorrados + " registros");

	}

	public void init(String codTerminal) throws Exception {
		log.info("init() - Inicializando Pinpad Worldline...");
		if (codTerminal == null) {
			throw new Exception("Es necesario especificar el ID del terminal en la configuración");
		}

		JC3ApiParams c3AgentParams = new JC3ApiParams();
		c3AgentParams.disableFileCheck();

		c3AgentParams.setPclStartBefore(true);
		c3AgentParams.setPclStopAfter(false);
		c3AgentParams.setPclCompanionType("USB");
		c3AgentParams.setPclBridgeTcpPort("9518");

		URI libUrl = Thread.currentThread().getContextClassLoader().getResource("lib").toURI();
		String lib = new File(libUrl).toString();
		String pclUrl = lib + "\\ext\\pcl";
		c3AgentParams.setPclDllPath(pclUrl);

		c3AgentParams.setC3NetStartBefore(false);
		c3AgentParams.setC3NetStopAfter(false);
		c3AgentParams.setC3NetAddress("127.0.0.1:9518");

		c3AgentParams.setC3EmbFilesType("all");
		c3AgentParams.setC3EmbFilesUpload(true);
		c3AgentParams.setC3EmbFilesDir("\\logs-pinpad");
		c3AgentParams.setC3EmbFilesPurge("14");

		c3AgentParams.setC3ApiExtended(true);
		c3Callbacks = new C3Callbacks();
		try {

			c3Agent = new JC3ApiInterfaceNet(c3Callbacks, c3AgentParams, log);
			JC3ApiC3Rspn respInit = c3Agent.processC3Init(codTerminal);
			checkResponse(respInit, INIT, false);

			// JC3ApiC3Rspn respInit = c3Agent.processC3InitWithOption(codTerminal, null, "1"); // INIT_NO_TMS
			// checkResponse(respInit, INIT_NO_TMS, false);
		}
		catch (WorldlineInitException e) {
			// No se ha hecho correctamente el INIT por lo que debe ser nulo para que en la siguiente transacción se
			// intente iniciar de nuevo
			c3Agent = null;
			throw e;
		}
		finally {
			c3Callbacks.closeAuxiliarScreen();
		}

	}

	public void pay(TicketVentaAbono ticket, String codTerminal, String codComercio, int contadorPagos, BigDecimal amount, DatosRespuestaPagoTarjeta response, int timeoutCancelPay)
	        throws Exception {
		log.debug("pay() - Iniciando petición para realizar pago en TPV...");

		if (c3Agent == null) {
			init(codTerminal);
		}

		BigDecimal montoCentimos = amount.multiply(BigDecimalUtil.CIEN);
		String userData1 = ticket.getCabecera().getCodTicket() + "-" + contadorPagos;
		c3Callbacks.setMostrarPantallaAux(true);
		JC3ApiC3Rspn respCard = c3Agent.processC3CardAcquisition(codTerminal, montoCentimos.longValue(), EUR_CURRENCY, userData1, "");
		checkResponse(respCard, LECTURA_TARJETA, false);
		String worldlinePaymentID = ((JC3ApiC3RspnExt) respCard).getcExtensionPaymentID();
		guardarFicheroWorldlinePayment(worldlinePaymentID, ticket.getCabecera().getUidTicket());

		JC3ApiC3Rspn respDebit = c3Agent.processC3DebitAfterCardAcquisition(codTerminal, montoCentimos.longValue(), EUR_CURRENCY, userData1, "");
		try {
			checkResponse(respDebit, PAY, false);
			c3Callbacks.closeAuxiliarScreen();
		}
		catch (WorldlinePaymentException e) {
			c3Callbacks.closeAuxiliarScreen();
			log.error(e.getMessage(), e);
			automaticCancel(ticket.getCabecera().getUidTicket(), codTerminal, timeoutCancelPay, false);
			throw e;
		}

		response.setTipoTransaccion("VENTA");
		saveResponse(response, respDebit, codComercio);
		salvar(sesionAplicacion.getUidActividad(), new Date(), PAY, amount);

		borrarFicheroWorldlinePayment(ticket.getCabecera().getUidTicket());
		log.debug("pay() - Finalizada petición para realizar pago en TPV.");
	}

	public void refund(TicketVentaAbono ticket, String codTerminal, String codComercio, int contadorPagos, BigDecimal amount, DatosRespuestaPagoTarjeta response) throws Exception {
		log.debug("refund() - Iniciando petición para realizar devolución en TPV...");

		if (c3Agent == null) {
			init(codTerminal);
		}

		BigDecimal montoCentimos = amount.multiply(BigDecimalUtil.CIEN);

		String userData1 = ticket.getCabecera().getCodTicket() + "-" + contadorPagos;
		String userData2 = "";

		JC3ApiC3Rspn respCardRefund = c3Agent.processC3CardAcquisitionBeforeRefund(codTerminal, montoCentimos.longValue(), EUR_CURRENCY, userData1, userData2);
		checkResponse(respCardRefund, LECTURA_TARJETA, false);
		String worldlinePaymentID = ((JC3ApiC3RspnExt) respCardRefund).getcExtensionPaymentID();
		guardarFicheroWorldlinePayment(worldlinePaymentID, ticket.getCabecera().getUidTicket());
		JC3ApiC3Rspn respRefund = c3Agent.processC3RefundAfterCardAcquisition(codTerminal, montoCentimos.longValue(), EUR_CURRENCY, userData1, userData2);
		c3Callbacks.closeAuxiliarScreen();
		checkResponse(respRefund, REFUND, false);

		response.setTipoTransaccion("DEVOLUCION");
		saveResponse(response, respRefund, codComercio);

		salvar(sesionAplicacion.getUidActividad(), new Date(), REFUND, amount);

		borrarFicheroWorldlinePayment(ticket.getCabecera().getUidTicket());
		log.debug("refund() - Finalizada petición para realizar devolución en TPV.");
	}

	public void cancelPay(PaymentDto payment, String codTerminal, boolean esProcesoAutomatico) throws Exception {
		log.debug("cancelPay() - Iniciando petición para realizar cancelación de pago en TPV...");

		if (c3Agent == null) {
			init(codTerminal);
		}

		// Generamos petición.
		DatosRespuestaPagoTarjeta datosRespuestaPago = (DatosRespuestaPagoTarjeta) payment.getExtendedData().get(BasicPaymentMethodManager.PARAM_RESPONSE_TEF);
		String paymentID = datosRespuestaPago.getAdicional("paymentID");

		JC3ApiC3Rspn respCard = c3Agent.processC3CancellationWithPaymentID("", codTerminal, "", "", paymentID, "1", "", "");
		c3Callbacks.closeAuxiliarScreen();
		checkResponse(respCard, CANCEL_PAY, esProcesoAutomatico);

		// Tan solo guardamos el total del CANCEL cuando el pago se registró en CZZ y por tanto en los totales, y
		// tenemos el amount.
		// Cuando el pago falla no se registra en CZZ ni en los totales, por lo que no hay que guardar nada en los
		// totales
		if (payment.getAmount() != null) {
			salvar(sesionAplicacion.getUidActividad(), new Date(), CANCEL_PAY, payment.getAmount());
		}

		log.debug("cancelPay() - Finalizada petición para realizar cancelación de pago en TPV.");
	}

	public void cancelRefund(TicketVentaAbono ticket, PaymentDto payment, String codTerminal, String codComercio, DatosRespuestaPagoTarjeta response) throws Exception {
		log.debug("cancelRefund() - Iniciando petición para realizar cancelación de pago en TPV...");

		if (c3Agent == null) {
			init(codTerminal);
		}
		DatosRespuestaPagoTarjeta datosRespuestaPago = (DatosRespuestaPagoTarjeta) payment.getExtendedData().get(BasicPaymentMethodManager.PARAM_RESPONSE_TEF);
		String tenderType = datosRespuestaPago.getAdicional("tenderType");
		String paymentID = datosRespuestaPago.getAdicional("paymentID");
		String userData1 = datosRespuestaPago.getAdicional("userData1");
		String userData2 = datosRespuestaPago.getAdicional("userData2");

		JC3ApiC3Rspn respCard = c3Agent.processC3CancellationWithPaymentID(tenderType, codTerminal, sesionCaja.getCajaAbierta().getCajaBean().getUsuario(), ticket.getCabecera().getCodTicket(),
		        paymentID, "1", userData1, userData2);
		checkResponse(respCard, CANCEL_REFUND, false);

		response.setTipoTransaccion("ANULACION");
		saveResponse(response, respCard, codComercio);

		salvar(sesionAplicacion.getUidActividad(), new Date(), CANCEL_REFUND, payment.getAmount());
		c3Callbacks.closeAuxiliarScreen();

		log.debug("cancelRefund() - Finalizada petición para realizar cancelación de pago en TPV.");
	}

	public void automaticCancel(String uidTicket, String codTerminal, int timeoutCancelPay, boolean esProcesoAutomatico) throws Exception {
		int countTimeout = 0;
		PaymentDto paymentDto = new PaymentDto();
		DatosRespuestaPagoTarjeta datosRespuesta = new DatosRespuestaPagoTarjeta();

		URL url = Thread.currentThread().getContextClassLoader().getResource("cancelaciones");
		File file = new File(url.getPath() + "\\" + uidTicket + ".txt");

		if (!file.exists()) {
			log.warn("automaticCancel() - No existe el fichero " + uidTicket + ".txt");
			return;
		}

		String worldlinePaymentID = new String(Files.readAllBytes(file.toPath()));

		datosRespuesta.addAdicional("paymentID", worldlinePaymentID);

		paymentDto.setExtendedData(new HashMap<>());

		paymentDto.getExtendedData().put(BasicPaymentMethodManager.PARAM_RESPONSE_TEF, datosRespuesta);

		while (countTimeout <= timeoutCancelPay) {
			try {
				cancelPay(paymentDto, codTerminal, esProcesoAutomatico);
				Thread.sleep(1000);
				break;
			}
			catch (Exception ignore) {
				countTimeout++;
			}

			if (countTimeout == timeoutCancelPay) {
				log.warn("automaticCancel() - El pago no se ha anotado en comerzzia. Se ha mandado la cancelacion automatica la cual ha fallado.");
				throw new PaymentException("Ha ocurrido un error y se ha intentado cancelar el pago, el cual ha fallado.\nEl pago no se ha anotado en Comerzzia.");
			}
		}
	}

	private void checkResponse(JC3ApiC3Rspn response, String operacion, Boolean esProcesoAutomatico) throws PaymentException {
		log.debug("readResponse() - Comprobando si la respuesta a la operacion " + operacion + " es correcta");

		if (response == null) {
			throw new WorldlinePaymentException("No se ha recibido respuesta por parte del banco");
		}

		if (!response.getcC3ErrorStr().equals("0000") && !response.getcC3ErrorStr().equals("0310") && !response.getcC3ErrorStr().equals("0311")) {
			String msg = "Ha ocurrido un error.\nPor favor, reintente transacción.";
			log.debug("readResponse() - " + msg);
			throw new WorldlinePaymentException(msg);
		}

		if (response.getcC3ErrorStr().equals("0310") && response.getcResponseCodeStr().equals("0017")) {
			String msg = "Operación cancelada por el usuario";
			log.debug("readResponse() - " + msg);
			throw new WorldlinePaymentException(msg);
		}

		if (response.getcC3ErrorStr().equals("0311") && response.getcResponseCodeStr().equals("0004")) {
			if (esProcesoAutomatico) {
				log.debug("readResponse() - El pago ha sido denegado, por lo que no es necesaria la cancelación");
				return;
			}
			else {
				String msg = "Ha ocurrido un error.\nPor favor, reintente transacción.";
				log.debug("readResponse() - " + msg);
				throw new WorldlinePaymentException(msg);
			}
		}

		if (response.getcC3ErrorStr().equals("0000")) {
			log.debug("readResponse() - Respuesta correcta para la operación de " + operacion);
		}
		else {
			String errorMsg = getErrorMsg(operacion);
			log.error("readResponse() - " + errorMsg + ":\n[C3ErrorStr: " + response.getcC3ErrorStr().trim() + ", C3ResponseCode: " + response.getcResponseCodeStr() + ", UserData1: "
			        + response.getcUserData1().trim() + ", UserData2: " + response.getcUserData2().trim() + "]");
			if (operacion.equals(INIT) || operacion.equals(INIT_NO_TMS)) {
				throw new WorldlineInitException(errorMsg);
			}
			else if (operacion.equals(PAY) || operacion.equals(REFUND)) {
				throw new WorldlinePaymentException(errorMsg);
			}
			else {
				throw new PaymentException(errorMsg);
			}
		}
	}

	private String getErrorMsg(String operacion) {
		String msg = "";
		switch (operacion) {
			case PAY:
				msg = "Ha ocurrido un error al realizar el pago";
				break;
			case REFUND:
				msg = "Ha ocurrido un error al realizar la devolución";
				break;
			case CANCEL_PAY:
				msg = "Ha ocurrido un error al cancelar el pago de venta";
				break;
			case CANCEL_REFUND:
				msg = "Ha ocurrido un error al cancelar el pago de la devolución";
				break;
			case LECTURA_TARJETA:
				msg = "Ha ocurrido un error al leer la tarjeta";
				break;
			case INIT:
				msg = "Ha ocurrido un error durante la inicialización del terminal";
				break;
			case INIT_NO_TMS:
				msg = "Ha ocurrido un error durante la inicialización sin el servidor TMS del terminal";
				break;
			default:
				msg = "Ha ocurrido un error con el terminal";
				break;
		}
		return msg;
	}

	private void saveResponse(DatosRespuestaPagoTarjeta datosRespuesta, JC3ApiC3Rspn response, String codComercio) {
		JC3ApiC3RspnExt extendedResponse = ((JC3ApiC3RspnExt) response);

		switch (extendedResponse.getcEntryMode()) {
			case "07": // Contactless
				datosRespuesta.setTipoLectura("L");
				break;
			case "05": // Chip
				datosRespuesta.setTipoLectura("E");
				break;
		}

		String pan = extendedResponse.getPAN();
		if (StringUtils.isNotBlank(pan)) {
			pan = "************" + pan.substring(pan.length() - 4, pan.length());
			datosRespuesta.setTarjeta(pan);
			datosRespuesta.setPAN(pan);
		}

		datosRespuesta.getDatosPeticion().setIdTransaccion(extendedResponse.getcUserData1().trim());
		datosRespuesta.setCodAutorizacion(extendedResponse.getAuthorizationNumber());
		datosRespuesta.setApplicationLabel(extendedResponse.getcExtensionApplicationLabel());
		datosRespuesta.setAID(extendedResponse.getcExtensionApplicationIdentifier());
		datosRespuesta.setCodigoCentro(sesionCaja.getCajaAbierta().getCodAlm());
		datosRespuesta.setCodAutorizacion(extendedResponse.getcNumAuto());
		datosRespuesta.setFuc(codComercio);
		datosRespuesta.setTerminalId(extendedResponse.getcTermNum());
		datosRespuesta.setNombreEntidad(extendedResponse.getcExtensionBankCode());
		datosRespuesta.setMsgRespuesta(extendedResponse.getMerchantTicket());

		Fecha fechaTransaccion = new Fecha(new Date());
		datosRespuesta.setFechaTransaccion(fechaTransaccion.getString("dd/MM/yyyy HH:mm:ss"));

		Map<String, String> adicionales = datosRespuesta.getAdicionales();
		if (adicionales == null) {
			adicionales = new HashMap<>();
		}

		adicionales.put("paymentID", extendedResponse.getcExtensionPaymentID());

		datosRespuesta.setAdicionales(adicionales);
	}

	private void guardarFicheroWorldlinePayment(String worldlinePaymentId, String uidTicket) throws IOException {
		log.debug("guardarFicheroWorldlinePayment() - Se va a guardar el fichero con nombre " + uidTicket + ".txt con el PaymentID " + worldlinePaymentId);
		URL url = Thread.currentThread().getContextClassLoader().getResource("cancelaciones");
		File file = new File(url.getPath() + "\\" + uidTicket + ".txt");
		log.debug("guardarFicheroWorldlinePayment() - Buscando fichero en la ruta: " + file.toString());
		InputStream is = new ByteArrayInputStream(worldlinePaymentId.getBytes());
		Files.copy(is, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}

	private void borrarFicheroWorldlinePayment(String uidTicket) {
		log.debug("borrarFicheroWorldlinePayment() - Se va a borrar el fichero " + uidTicket + ".txt");
		URL url = Thread.currentThread().getContextClassLoader().getResource("cancelaciones");
		File file = new File(url.getPath() + "\\" + uidTicket + ".txt");
		file.delete();
	}

	public void closeAuxiliarScreen() {
		c3Callbacks.closeAuxiliarScreen();
	}

}
