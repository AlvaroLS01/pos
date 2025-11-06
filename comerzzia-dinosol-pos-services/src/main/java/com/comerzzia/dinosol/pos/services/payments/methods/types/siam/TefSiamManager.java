package com.comerzzia.dinosol.pos.services.payments.methods.types.siam;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.core.util.numeros.Numero;
import com.comerzzia.dinosol.pos.services.mediospago.ParseadorTransacciones;
import com.comerzzia.dinosol.pos.services.mediospago.SiamService;
import com.comerzzia.dinosol.pos.services.payments.methods.types.siam.dto.errores.SiamError;
import com.comerzzia.pos.core.dispositivos.dispositivo.tarjeta.TarjetaException;
import com.comerzzia.pos.services.payments.PaymentDto;
import com.comerzzia.pos.services.payments.PaymentException;
import com.comerzzia.pos.services.payments.configuration.ConfigurationPropertyDto;
import com.comerzzia.pos.services.payments.configuration.PaymentMethodConfiguration;
import com.comerzzia.pos.services.payments.events.PaymentInitEvent;
import com.comerzzia.pos.services.payments.events.PaymentOkEvent;
import com.comerzzia.pos.services.payments.methods.types.BasicPaymentMethodManager;
import com.comerzzia.pos.services.ticket.pagos.PagoTicket;
import com.comerzzia.pos.services.ticket.pagos.tarjeta.DatosPeticionPagoTarjeta;
import com.comerzzia.pos.services.ticket.pagos.tarjeta.DatosRespuestaPagoTarjeta;
import com.comerzzia.pos.util.i18n.I18N;

@SuppressWarnings({"unchecked"})
@Scope("prototype")
@Component
public class TefSiamManager extends BasicPaymentMethodManager {

	private static final Logger log = Logger.getLogger(TefSiamManager.class);

	//campos de configuración del pinpad
	private static final String TOUT1 = "timeout_pinpad_siam";
	private static final String TOUT2 = "timeout_servidor_siam";
	private static final String HOST = "host";
	private static final String PUERTO = "puerto_servidor_siam";
	private static final String ID_TERMINAL = "id_terminal_siam";
	private static final String CLIENTE = "cliente";

	private static final String PARAM_NUMOP = "SiamNumOp";
	
	//Operaciones
	public static final String VENTA = "VT";
	public static final String DEVOLUCION = "DV";
	public static final String CANCEL = "AN";
	public static final String CANCEL_PAY = "AV";
	public static final String CANCEL_RETURN = "AD";
	public static final String INIT = "IN";
	public static final String TEST_TERMINAL = "PP";
		
	//Interfaces
	public static final String INTEFAZ_EMV = "X";
	public static final String INTEFAZ_INIT = "0";
	
	//Mensajes Confirmación
	public static final String OK = "OK";
	public static final String KO = "KO";

	public static final int LONGITUD_MSG_TCP = 1505;
	
	private Integer tout1;
	private Integer tout2;
	private String host;
	private int puerto;
	private String idTerminal;
	private String cliente;

	@Autowired
	protected SiamService siamService;
	
	@Autowired
	protected ParseadorTransacciones transaccion;

	@Override
	public List<ConfigurationPropertyDto> getConfigurationProperties() {
		List<ConfigurationPropertyDto> properties = new ArrayList<ConfigurationPropertyDto>();
		properties.add(new ConfigurationPropertyDto(CLIENTE, I18N.getTexto("Cliente")));
		properties.add(new ConfigurationPropertyDto(ID_TERMINAL, I18N.getTexto("ID Terminal")));
		properties.add(new ConfigurationPropertyDto(HOST, I18N.getTexto("Host")));
		properties.add(new ConfigurationPropertyDto(PUERTO, I18N.getTexto("Puerto")));
		properties.add(new ConfigurationPropertyDto(TOUT1, I18N.getTexto("Timeout pinpad (seg)")));
		properties.add(new ConfigurationPropertyDto(TOUT2, I18N.getTexto("Timeout servidor (seg)")));
		return properties;
	}

	@Override
	public void setConfiguration(PaymentMethodConfiguration configuration) {
		super.setConfiguration(configuration);

		host = configuration.getConfigurationProperty(HOST);
		String puertoConf = configuration.getConfigurationProperty(PUERTO);
		if (StringUtils.isNotBlank(puertoConf)) {
			puerto = Numero.desformateaInteger(puertoConf, null);
		}
		idTerminal = configuration.getConfigurationProperty(ID_TERMINAL);
		if (StringUtils.isNotBlank(idTerminal) && idTerminal.length() == 1) {
			log.debug("setConfiguration() - El ID Terminal indicado solo tiene un dígito por lo que se le añadirá un 0 delante.");
			idTerminal = "0" + idTerminal;
		}

		String timeoutPinpadStr = configuration.getConfigurationProperty(TOUT1);

		if (StringUtils.isNotBlank(timeoutPinpadStr)) {
			tout1 = Integer.parseInt(timeoutPinpadStr.trim());
		}
		else {
			tout1 = 60;
		}

		String timeoutServidorStr = configuration.getConfigurationProperty(TOUT2);

		if (StringUtils.isNotBlank(timeoutServidorStr)) {
			tout2 = Integer.parseInt(timeoutServidorStr.trim());
		}
		else {
			tout2 = 60;
		}

		cliente = configuration.getConfigurationProperty(CLIENTE);

		log.debug("getConfigurationProperties() - Configuración de Siam: ");
		log.debug("getConfigurationProperties() - Host: " + host);
		log.debug("getConfigurationProperties() - Puerto: " + puerto);
		log.debug("getConfigurationProperties() - Timeout Pinpad en seg: " + tout1);
		log.debug("getConfigurationProperties() - Timeout Servidor en seg: " + tout2);
		log.debug("getConfigurationProperties() - Id del terminal: " + idTerminal);
		log.debug("getConfigurationProperties() - Cliente: " + cliente);

		try {
			transaccion.init(idTerminal, cliente);
			siamService.init(getHost(), getPuerto(), tout1, tout2, idTerminal, cliente);
		}
		catch (IOException e) {
			log.debug("setConfiguration() - error al inicialiazar SiamService" + e.getMessage(), e);
		}
	}

	@Override
	public boolean pay(BigDecimal amount) throws PaymentException {
		log.debug("pay() - Realizando operación de venta");

		PaymentInitEvent initEvent = new PaymentInitEvent(this);
		getEventHandler().paymentInitProcess(initEvent);

		/* Variables para crear un objeto de tipo "DatosPeticionPagoTarjeta" */
		String codigoTicket = ticket.getCabecera().getCodTicket();
		Long idTicket = ticket.getCabecera().getIdTicket();
		/* Creamos los datos de petición y de respuesta. */
		DatosPeticionPagoTarjeta datosPeticion = new DatosPeticionPagoTarjeta(codigoTicket, idTicket, amount);
		DatosRespuestaPagoTarjeta datosRespuesta = null;

		try {
			String mensajeTransaccion = transaccion.crearPeticion(datosPeticion.getImporte(), VENTA, null, ticket, paymentId);
			log.debug("pay() - mensaje transaccion " + mensajeTransaccion);

			enviarPeticionVenta(mensajeTransaccion);
			
			log.debug("pay() - respuesta de peticion " + siamService.getRespuesta());

			if (siamService.esRespuestaOK(siamService.getRespuesta())) {
				datosRespuesta = procesarRespuestaCorrecta(datosPeticion, null);
			}
			else if (StringUtils.isBlank(siamService.getRespuesta())) {
				log.debug("pay() - tratando respuesta en blanco");
				reenviarPeticionVenta(mensajeTransaccion);
				datosRespuesta = procesarReenvioPeticion(datosPeticion, null);
			}
			else if (siamService.esRespuestaKO(siamService.getRespuesta())) {
				datosRespuesta = tratarRespuestaErronea(datosPeticion);
			}
			else {
				throw new TarjetaException(I18N.getTexto("No se ha podido realizar la comunicación con el servidor del TEF. Por favor, contacte con el administrador si el problema persiste."),
				        datosRespuesta);
			}

			generarEventoOk(amount, datosRespuesta);
			return true;

		}
		catch (Exception e) {
			log.error("pay() - Ha habido un error al realizar la operación: " + e.getMessage(), e);
			throw new PaymentException(e.getMessage(), e, paymentId, this);
		}
	}

	private DatosRespuestaPagoTarjeta procesarReenvioPeticion(DatosPeticionPagoTarjeta datosPeticion, DatosRespuestaPagoTarjeta datosRespuestaOrigen) throws TarjetaException {
		DatosRespuestaPagoTarjeta datosRespuesta = null;
		if (StringUtils.isNotBlank(siamService.getRespuesta()) && !siamService.esRespuestaKO(siamService.getRespuesta()) && !siamService.esRespuestaKOVT(siamService.getRespuesta())) {
			datosRespuesta = transaccion.procesarRespuesta(siamService.getRespuesta(), datosPeticion, datosRespuestaOrigen);
		}
		else {
			throw new TarjetaException(I18N.getTexto("No se ha podido realizar la comunicación con el servidor del TEF. Por favor, contacte con el administrador si el problema persiste."),
			        datosRespuesta);
		}
		return datosRespuesta;
	}

	private DatosRespuestaPagoTarjeta procesarRespuestaCorrecta(DatosPeticionPagoTarjeta datosPeticion, DatosRespuestaPagoTarjeta datosRespuestaOrigen) throws TarjetaException {
		log.debug("procesando respuesta correcta" + siamService.getRespuesta());
		siamService.setRespuestaOriginal(siamService.getRespuesta());
		DatosRespuestaPagoTarjeta datosRespuesta = transaccion.procesarRespuesta(siamService.getRespuesta(), datosPeticion, datosRespuestaOrigen);
		return datosRespuesta;
	}

	private DatosRespuestaPagoTarjeta tratarRespuestaErronea(DatosPeticionPagoTarjeta datosPeticion) throws TarjetaException {
		DatosRespuestaPagoTarjeta datosRespuesta;
		datosRespuesta = transaccion.procesarRespuesta(siamService.getRespuesta(), datosPeticion, null);
		if(datosRespuesta.getCodResult().contains("KO")) {
			throw new TarjetaException(I18N.getTexto(crearMensajeError(datosRespuesta)).toUpperCase(), datosRespuesta);
		}
		return datosRespuesta;
	}
	
	private DatosRespuestaPagoTarjeta tratarRespuestaErroneaCancelacion(DatosPeticionPagoTarjeta datosPeticion) throws TarjetaException {
		DatosRespuestaPagoTarjeta datosRespuesta;
		datosRespuesta = transaccion.procesarRespuestaCancelacionPago(siamService.getRespuesta(), datosPeticion);
		if(datosRespuesta.getCodResult().contains("KO")) {
			throw new TarjetaException(I18N.getTexto(crearMensajeError(datosRespuesta)).toUpperCase(), datosRespuesta);
		}
		return datosRespuesta;
	}

	private void reenviarPeticionVenta(String mensajeTransaccion) throws Exception {
		siamService.crearServicioVenta(siamService.modificarMensajeParaReenvioPeticion(mensajeTransaccion));
		siamService.realizarConexion();
	}

	private String crearMensajeError(DatosRespuestaPagoTarjeta datosRespuesta) {
		SiamError error = new SiamError();
		error.setearDatosRespuestaError(siamService.getRespuesta());
		String msgeError = error.getMapaErrores().get(datosRespuesta.getMsgRespuesta() != null ? datosRespuesta.getCodResult() : "N1");
		return msgeError;
	}

	private void enviarPeticionVenta(String mensajeTransaccion) throws Exception {
		log.debug("Se va a realizar peticion VENTA");
		siamService = siamService.crearServicioVenta(mensajeTransaccion);
		siamService.realizarConexion();
	}

	protected void generarEventoOk(BigDecimal amount, DatosRespuestaPagoTarjeta datosRespuesta) {
		PaymentOkEvent event = new PaymentOkEvent(this, paymentId, amount);
		event.addExtendedData(PARAM_RESPONSE_TEF, datosRespuesta);
		// event.addExtendedData(PARAM_NUMOP, datosRespuesta.getNumOperacion());
		getEventHandler().paymentOk(event);
	}

	@Override
	public boolean returnAmount(BigDecimal amount) throws PaymentException {
		log.debug("returnAmount() - Realizando operación de devolución");

		/* Variables para crear un objeto de tipo "DatosPeticionPagoTarjeta" */
		String codigoTicket = ticket.getCabecera().getCodTicket();
		Long codigoDocumento = ticket.getCabecera().getIdTicket();

		String numOperacion = "";
		if (ticketOrigen != null) {
			for (PagoTicket pago : (List<PagoTicket>) ticketOrigen.getPagos()) {
				String numOperacionOrigen = (String) pago.getExtendedData(PARAM_NUMOP);
				if (pago.getCodMedioPago().equals(paymentCode) && StringUtils.isNotBlank(numOperacionOrigen)) {
					if (StringUtils.isBlank(numOperacion) && StringUtils.isNotBlank(numOperacionOrigen)) {
						numOperacion = numOperacionOrigen;
					}
					else {
						numOperacion = "";
					}
				}
			}
		}

		/* Creamos los datos de petición y de respuesta. */
		DatosPeticionPagoTarjeta datosDevolucion = new DatosPeticionPagoTarjeta(codigoTicket, codigoDocumento, amount);
		DatosRespuestaPagoTarjeta datosRespuesta = (DatosRespuestaPagoTarjeta) parameters.get("PAGO_SELECCIONADO");

		try {

			String mensajeTransaccion = transaccion.crearPeticion(amount, DEVOLUCION, datosRespuesta, ticket, paymentId);
			log.debug("returnAmount() - mensaje transaccion " + mensajeTransaccion);

			enviarPeticionDevolucion(mensajeTransaccion);

			log.debug("returnAmount() - respuesta de peticion " + siamService.getRespuesta());

			if (siamService.esRespuestaOK(siamService.getRespuesta())) {
				datosRespuesta = procesarRespuestaCorrecta(datosDevolucion, datosRespuesta);
			}
			else if (StringUtils.isBlank(siamService.getRespuesta())) {
				log.debug("returnAmount() - tratando respuesta en blanco");
				reenviarPeticionDevolucion(mensajeTransaccion);
				datosRespuesta = procesarReenvioPeticion(datosDevolucion, datosRespuesta);
			}
			else if (siamService.esRespuestaKO(siamService.getRespuesta())) {
				log.debug("returnAmount() - tratando respuesta erronea");
				datosRespuesta = tratarRespuestaErronea(datosDevolucion);
			}
			else {
				throw new TarjetaException(I18N.getTexto("No se ha podido realizar la comunicación con el servidor del TEF. Por favor, contacte con el administrador si el problema persiste."),
				        datosRespuesta);
			}
			generarEventoOk(amount, datosRespuesta);
			return true;

		}
		catch (Exception e) {
			log.error("returnAmount() - Ha habido un error al realizar la operación: " + e.getMessage(), e);
			throw new PaymentException(e.getMessage(), e, paymentId, this);
		}
	}

	private void reenviarPeticionDevolucion(String mensajeTransaccion) throws Exception {
		siamService.crearServicioDevolucion(siamService.modificarMensajeParaReenvioPeticion(mensajeTransaccion));
		siamService.realizarConexion();
	}

	private void enviarPeticionDevolucion(String mensajeTransaccion) throws Exception {
		log.debug("returnAmount() - Se va a realizar peticion DEVOLUCION");
		siamService = siamService.crearServicioDevolucion(mensajeTransaccion);
		siamService.realizarConexion();
	}

	@Override
	public boolean cancelPay(PaymentDto payment) throws PaymentException { // HACEMOS LA MISMA OPERACION QUE cancelReturn

		log.debug("cancelPay() - Solicitando anulación de pago");

		/* Variables para crear un objeto de tipo "DatosPeticionPagoTarjeta" */
		String codigoTicket = ticket.getCabecera().getCodTicket();
		Long codigoDocumento = ticket.getCabecera().getIdTicket();
		/* Creamos los datos de petición y de respuesta. */
		DatosPeticionPagoTarjeta datosAnulacionVenta = new DatosPeticionPagoTarjeta(codigoTicket, codigoDocumento, payment.getAmount());
		DatosRespuestaPagoTarjeta datosRespuestaOrigen = (DatosRespuestaPagoTarjeta) payment.getExtendedData().get(PARAM_RESPONSE_TEF);
		datosAnulacionVenta.setCodAutorizacion(datosRespuestaOrigen.getCodAutorizacion());
		datosAnulacionVenta.setEmpleado(datosRespuestaOrigen.getEmpleado());
		datosAnulacionVenta.setNumOpBanco(datosRespuestaOrigen.getNumOperacionBanco());
		datosAnulacionVenta.setNumOperacion(datosRespuestaOrigen.getNumOperacion());

		try {
			DatosRespuestaPagoTarjeta datosRespuesta = null;

			// usamos la secuencia guardada en el adicional del respuesta origen seteado en el evenOk del pay
			String mensajeTransaccion = transaccion.crearPeticion(null, CANCEL, datosRespuestaOrigen, ticket, paymentId);
			log.debug("cancelPay() - mensaje transaccion " + mensajeTransaccion);

			enviarPeticionAnulacionPagoVenta(mensajeTransaccion);

			log.debug("cancelPay() - respuesta de peticion " + siamService.getRespuesta());

			if (siamService.esRespuestaOK(siamService.getRespuesta())) {
				datosRespuesta = procesarRespuestaCancelacion(datosAnulacionVenta);
			}
			else if (StringUtils.isBlank(siamService.getRespuesta())) {
				log.debug("cancelPay() - tratando respuesta en blanco");
				reenviarPeticionCancelacionPago(mensajeTransaccion);
				datosRespuesta = procesarReenvioPeticion(datosAnulacionVenta, null);
			}
			else if (siamService.esRespuestaKO(siamService.getRespuesta())) {
				log.debug("cancelPay() - tratando respuesta erronea");
				datosRespuesta = tratarRespuestaErroneaCancelacion(datosAnulacionVenta);
			}
			else {
				throw new TarjetaException(I18N.getTexto("No se ha podido realizar la comunicación con el servidor del TEF. Por favor, contacte con el administrador si el problema persiste."),
				        datosRespuesta);
			}

			/* Generar el evento de confirmación */
			PaymentOkEvent event = new PaymentOkEvent(this, payment.getPaymentId(), payment.getAmount());
			event.setCanceled(true);
			event.addExtendedData(PARAM_RESPONSE_TEF, datosRespuesta);
			getEventHandler().paymentOk(event);

			return true;
		}
		catch (Exception e) {
			log.error("cancelPay() - Ha habido un error al realizar la operación: " + e.getMessage(), e);
			throw new PaymentException(e.getMessage(), e, paymentId, this);
		}
	}

	private void reenviarPeticionCancelacionPago(String mensajeTransaccion) throws Exception {
		siamService.crearServicioAnulacionPago(siamService.modificarMensajeParaReenvioPeticion(mensajeTransaccion));
		siamService.realizarConexion();
	}

	private DatosRespuestaPagoTarjeta procesarRespuestaCancelacion(DatosPeticionPagoTarjeta datosAnulacionVenta) throws TarjetaException {
		DatosRespuestaPagoTarjeta datosRespuesta;
		log.debug("procesando respuesta " + siamService.getRespuesta());
		datosRespuesta = transaccion.procesarRespuestaCancelacionPago(siamService.getRespuesta(), datosAnulacionVenta);
		return datosRespuesta;
	}

	private void enviarPeticionAnulacionPagoVenta(String mensajeTransaccion) throws Exception {
		log.debug("enviarPeticionAnulacionPagoVenta() - Se va a realizar peticion ANULACION VENTA");
		siamService = siamService.crearServicioAnulacionPago(mensajeTransaccion);
		siamService.realizarConexion();
	}

	@Override
	public boolean cancelReturn(PaymentDto payment) throws PaymentException { // HACEMOS LA MISMA OPERACION QUE cancelPay

		String codigoTicket = ticket.getCabecera().getCodTicket();
		Long codigoDocumento = ticket.getCabecera().getIdTicket();
		/* Creamos los datos de petición y de respuesta. */
		DatosPeticionPagoTarjeta datosAnulacionDevolucion = new DatosPeticionPagoTarjeta(codigoTicket, codigoDocumento, payment.getAmount());
		DatosRespuestaPagoTarjeta datosRespuestaOrigen = (DatosRespuestaPagoTarjeta) payment.getExtendedData().get(PARAM_RESPONSE_TEF);
		datosAnulacionDevolucion.setCodAutorizacion(datosRespuestaOrigen.getCodAutorizacion());
		datosAnulacionDevolucion.setEmpleado(datosRespuestaOrigen.getEmpleado());
		datosAnulacionDevolucion.setNumOpBanco(datosRespuestaOrigen.getNumOperacionBanco());
		datosAnulacionDevolucion.setNumOperacion(datosRespuestaOrigen.getNumOperacion());

		try {
			DatosRespuestaPagoTarjeta datosRespuesta = null;

			// usamos la secuencia guardada en el adicional del respuesta origen seteado en el evenOk del pay
			String mensajeTransaccion = transaccion.crearPeticion(null, CANCEL, datosRespuestaOrigen, ticket, paymentId);
			log.debug("cancelReturn() - mensaje transaccion " + mensajeTransaccion);

			enviarPeticionCancelacionDevolucion(mensajeTransaccion);

			log.debug("cancelReturn() - respuesta de peticion " + siamService.getRespuesta());

			if (siamService.esRespuestaOK(siamService.getRespuesta())) {
				datosRespuesta = procesarRespuestaCancelacion(datosAnulacionDevolucion);
			}
			else if (StringUtils.isBlank(siamService.getRespuesta())) {
				log.debug("cancelReturn() - tratando respuesta en blanco");
				reenviarPeticionCancelacionDevolucion(mensajeTransaccion);
				datosRespuesta = procesarReenvioPeticion(datosAnulacionDevolucion, null);
			}
			else if (siamService.esRespuestaKO(siamService.getRespuesta())) {
				log.debug("cancelReturn() - tratando respuesta erronea");
				datosRespuesta = tratarRespuestaErronea(datosAnulacionDevolucion);
			}
			else {
				throw new TarjetaException(I18N.getTexto("No se ha podido realizar la comunicación con el servidor del TEF. Por favor, contacte con el administrador si el problema persiste."),
				        datosRespuesta);
			}

			/* Generar el evento de conformación */
			PaymentOkEvent event = new PaymentOkEvent(this, paymentId, payment.getAmount());
			event.setCanceled(true);
			event.addExtendedData(PARAM_RESPONSE_TEF, datosRespuesta);
			getEventHandler().paymentOk(event);
			return true;

		}
		catch (Exception e) {
			log.error("cancelReturn() - Ha habido un error al realizar la operación:" + e.getMessage(), e);
			throw new PaymentException(e.getMessage(), e, paymentId, this);
		}
	}

	private void reenviarPeticionCancelacionDevolucion(String mensajeTransaccion) throws Exception {
		siamService.crearServicioAnulacionPago(siamService.modificarMensajeParaReenvioPeticion(mensajeTransaccion));
		siamService.realizarConexion();
	}

	private void enviarPeticionCancelacionDevolucion(String mensajeTransaccion) throws Exception {
		log.debug("cancelReturn() - Se va a realizar peticion ANULACION");
		siamService = siamService.crearServicioAnulacionPago(mensajeTransaccion);
		siamService.realizarConexion();
	}

	public Integer getTout1() {
		return tout1;
	}

	public void setTout1(Integer tout1) {
		this.tout1 = tout1;
	}

	public Integer getTout2() {
		return tout2;
	}

	public void setTout2(Integer tout2) {
		this.tout2 = tout2;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPuerto() {
		return puerto;
	}

	public void setPuerto(int puerto) {
		this.puerto = puerto;
	}

	public String getIdTerminal() {
		return idTerminal;
	}

	public void setIdTerminal(String idTerminal) {
		this.idTerminal = idTerminal;
	}

	public String getCodCliente() {
		return cliente;
	}

	public void setCodCliente(String codCliente) {
		this.cliente = codCliente;
	}

}
