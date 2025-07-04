package com.comerzzia.cardoso.pos.devices.dispositivo.tarjeta.conexflow;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.dispositivos.dispositivo.DispositivoException;
import com.comerzzia.pos.core.dispositivos.dispositivo.tarjeta.TarjetaException;
import com.comerzzia.pos.services.payments.PaymentDto;
import com.comerzzia.pos.services.payments.PaymentException;
import com.comerzzia.pos.services.payments.configuration.ConfigurationPropertyDto;
import com.comerzzia.pos.services.payments.configuration.PaymentMethodConfiguration;
import com.comerzzia.pos.services.payments.events.PaymentInitEvent;
import com.comerzzia.pos.services.payments.events.PaymentOkEvent;
import com.comerzzia.pos.services.ticket.pagos.tarjeta.DatosPeticionPagoTarjeta;
import com.comerzzia.pos.services.ticket.pagos.tarjeta.DatosRespuestaPagoTarjeta;
import com.comerzzia.pos.util.i18n.I18N;
import java.nio.charset.StandardCharsets;

@Component
@Scope("prototype")
public class CardosoTefConexflow extends com.comerzzia.pos.dispositivo.tarjeta.conexflow.metodosconexion.TefConexflowUSBManager{

	private static final Logger log = Logger.getLogger(CardosoTefConexflow.class);
	
	public static final String ERROR_CONNECT_REFUSED = "Connection refused: connect";
	
	public static final String REQUEST_CODE          = "PE01FN0002";
	public static final String REQUEST_CODE_TOTAL    = "PE04FN000225";
	
	public static final String TOTALES               = "TOTALES";
	
	public static final SimpleDateFormat formatFecha = new SimpleDateFormat("yyyyMMdd");
	public static final SimpleDateFormat formatHora = new SimpleDateFormat("HHmmss");
	
	/* ############################################################################################# */
	/* ######################################### CONEXIONES ######################################## */
	/* ############################################################################################# */
	
	public void connect() throws DispositivoException{
		log.info("connect() - Iniciando la conexión con el servicio de VisualPlugin de Conexflow...");
		
		try{
			/* Siempre desconectamos antes de conectar, por si existe alguna conexión abierta. */
			disconnect();
			/* Generamos la conexión TCP con Conexflow. */
			socket = new Socket(servidorTCP, new Integer(puertoTCP));
			socket.setSoTimeout(new Integer(timeout) * 1000);
		}
		catch(Exception e){
			String msgError = "Error al realizar la conexión con Conexflow : " + e.getMessage();
			log.error("connect() - " + msgError, e);
			throw new DispositivoException(msgError, e);
		}
	}
	
	public void disconnect() throws DispositivoException{
		log.info("disconnect() - Iniciando la desconexión con el servicio de VisualPlugin de Conexflow...");
		
		try{
			/* En caso de tener la conexión iniciada, la cerramos. */
			if(socket != null){
				socket.close();
				socket = null;
			}
		}
		catch(Exception e){
			String msgError = "Error al realizar la desconexión con Conexflow : " + e.getMessage();
			log.error("disconnect() - " + msgError, e);
			throw new DispositivoException(msgError, e);
		}
	}
	
	/* ############################################################################################# */
	/* ############################################ PAGOS ########################################## */
	/* ############################################################################################# */
	
	@Override
	public boolean pay(BigDecimal payment) throws PaymentException{
		log.info("pay() - Iniciando pago en servicio VisualPlugin de Conexflow...");
		
		initialSetup();
		
		intTransactionID = ticket.getCabecera().getCodTicket().replaceAll("[a-zA-Z]", "").replaceAll("/", "");
		
		PaymentInitEvent initEvent = new PaymentInitEvent(this);
		getEventHandler().paymentInitProcess(initEvent);
		
		DatosPeticionPagoTarjeta requestData = new DatosPeticionPagoTarjeta(ticket.getCabecera().getCodTicket(), ticket.getCabecera().getIdTicket(), payment);
		DatosRespuestaPagoTarjeta responseData = new DatosRespuestaPagoTarjeta(requestData);
		responseData.setTipoTransaccion(VENTA);
		
		try{
			connect();
			
			String request = generateRequest(VENTA, requestData);
			log.info("pay() - VENTA - REQUEST CONEXFLOW : " + request);
			
			String response = sendRequest(request);
			log.info("pay() - VENTA - RESPONSE CONEXFLOW : " + response);
			if(response.length() < 12){
				throw new RuntimeException(I18N.getTexto("Respuesta incorrecta del terminal de pago. Verifique que está preparado."));
			}
			
			saveResponse(responseData, response);
			processResponseTransactionPayment(responseData);
			
			transaccionBienTerminada = true;
			
			setPaymentOkEvent(payment, responseData);
		}
		catch(Exception e){
			String msgError = e.getMessage();
			log.error("pay() - " + msgError, e);
			
			if(e instanceof SocketTimeoutException){
				msgError = I18N.getTexto("HA EXPIRADO EL TIEMPO DE ESPERA AL SERVIDOR DE CONEXFLOW");
			}
			else if(e.getMessage().equals(ERROR_CONNECT_REFUSED)){
				msgError = I18N.getTexto("NO HA SIDO POSIBLE ESTABLECER LA COMUNICACIÓN CON EL PINPAD. "
						+ "\n\n ASEGÚRESE DE QUE EL PINPAD ESTÁ CONECTADO Y EL SERVICIO VISUAL PLUGIN ENCENDIDO");
			}
			
			throw new PaymentException(e.getMessage(), e, paymentId, this);
		}
		finally{
			try{
				log.info("pay() - CONFIRMACION - REQUEST CONEXFLOW : " + "O");
				sendRequest("O");
			}
			catch(Exception e){
				log.error("pay() - " + e.getMessage());
			}
			try{
				disconnect();
			}
			catch(Exception e){
				log.error("pay() - " + e.getMessage());
			}
		}
		
		return true;
	}
	
	@Override
	public boolean cancelPay(PaymentDto payment) throws PaymentException{
		log.info("cancelPay() - Iniciando cancelación en servicio VisualPlugin de Conexflow...");
		
		initialSetup();
		
		intTransactionID = (String) ((DatosRespuestaPagoTarjeta) payment.getExtendedData().get(PARAM_RESPONSE_TEF)).getAdicional("EM");
		
		DatosPeticionPagoTarjeta datosPeticion =  new DatosPeticionPagoTarjeta(ticket.getCabecera().getCodTicket(), ticket.getCabecera().getIdTicket(), payment.getAmount());
		DatosRespuestaPagoTarjeta datosRespuesta = new DatosRespuestaPagoTarjeta(datosPeticion);
		datosRespuesta.setTipoTransaccion(ANULACION);
		
		try{
			connect();
			
			if((payment != null) && (StringUtils.isBlank(codResultado) || transaccionBienTerminada)){
				String request = generateRequest(ANULACION, null);
				log.info("cancelPay() - ANULACION VENTA - REQUEST CONEXFLOW : " + request);
				
				String response = sendRequest(request);
				log.info("cancelPay() - ANULACION VENTA - RESPONSE CONEXFLOW : " + response);
			}
			
			PaymentOkEvent event = new PaymentOkEvent(this, payment.getPaymentId(), payment.getAmount());
			event.setCanceled(true);
			event.addExtendedData(PARAM_RESPONSE_TEF, datosRespuesta);
			getEventHandler().paymentOk(event);
		}
		catch(Exception e){
			throw new PaymentException(e.getMessage(), e, paymentId, this);
		}
		finally{
			try{
				log.info("cancelPay() - CONFIRMACION - REQUEST CONEXFLOW : " + "O");
				sendRequest("O");
			}
			catch(Exception e){
				log.error("cancelPay() - " + e.getMessage());
			}
			try{
				disconnect();
			}
			catch(Exception e){
				log.error("cancelPay() - " + e.getMessage());
			}
		}
		
		return true;
	}

	/* ############################################################################################# */
	/* ####################################### DEVOLUCIONES ######################################## */
	/* ############################################################################################# */
	
	@Override
	public boolean returnAmount(BigDecimal payment) throws PaymentException{
		log.info("returnAmount() - Iniciando devolución en sercicio VisualPlugin de Conexflow...");
		
		initialSetup();
		
		intTransactionID = ticket.getCabecera().getCodTicket().replaceAll("[a-zA-Z]", "").replaceAll("/", "");
		
		PaymentInitEvent initEvent = new PaymentInitEvent(this);
		getEventHandler().paymentInitProcess(initEvent);
		
		DatosPeticionPagoTarjeta requestData = new DatosPeticionPagoTarjeta(ticket.getCabecera().getCodTicket(), ticket.getCabecera().getIdTicket(), payment);
		DatosRespuestaPagoTarjeta responseData = new DatosRespuestaPagoTarjeta(requestData);
		responseData.setTipoTransaccion(DEVOLUCION);
		
		try{
			connect();
			
			String request = generateRequest(DEVOLUCION, requestData);
			log.info("returnAmount() - DEVOLUCION - REQUEST CONEXFLOW : " + request);
			
			String response = sendRequest(request);
			log.info("returnAmount() - DEVOLUCION - RESPONSE CONEXFLOW : " + response);
			if(response.length() < 12){
				throw new RuntimeException(I18N.getTexto("Respuesta incorrecta del terminal de pago. Verifique que está preparado."));
			}
			
			saveResponse(responseData, response);
			processResponseTransactionPayment(responseData);
			
			transaccionBienTerminada = true;
			
			setPaymentOkEvent(payment, responseData);
		}
		catch(Exception e){
			String msgError = e.getMessage();
			log.error("returnAmount() - " + msgError, e);
			
			if(e instanceof SocketTimeoutException){
				msgError = I18N.getTexto("HA EXPIRADO EL TIEMPO DE ESPERA AL SERVIDOR DE CONEXFLOW");
			}
			else if(e.getMessage().equals(ERROR_CONNECT_REFUSED)){
				msgError = I18N.getTexto("NO HA SIDO POSIBLE ESTABLECER LA COMUNICACIÓN CON EL PINPAD. "
						+ "\n\n ASEGÚRESE DE QUE EL PINPAD ESTÁ CONECTADO Y EL SERVICIO VISUAL PLUGIN ENCENDIDO");
			}
			
			throw new PaymentException(e.getMessage(), e, paymentId, this);
		}
		finally{
			try{
				log.info("returnAmount() - CONFIRMACION - REQUEST CONEXFLOW : " + "O");
				sendRequest("O");
			}
			catch(Exception e){
				log.error("returnAmount() - " + e.getMessage());
			}
			try{
				disconnect();
			}
			catch(Exception e){
				log.error("returnAmount() - " + e.getMessage());
			}
		}
		
		return true;
	}
	
	@Override
	public boolean cancelReturn(PaymentDto payment) throws PaymentException{
		log.info("cancelReturn() - Iniciando cancelación de devolución en sercicio VisualPlugin de Conexflow...");
		
		initialSetup();
		
		intTransactionID = (String) ((DatosRespuestaPagoTarjeta) payment.getExtendedData().get(PARAM_RESPONSE_TEF)).getAdicional("EM");
		
		DatosPeticionPagoTarjeta datosPeticion =  new DatosPeticionPagoTarjeta(ticket.getCabecera().getCodTicket(), ticket.getCabecera().getIdTicket(), payment.getAmount());
		DatosRespuestaPagoTarjeta datosRespuesta = new DatosRespuestaPagoTarjeta(datosPeticion);
		datosRespuesta.setTipoTransaccion(ANULACION);
		
		try{
			connect();
			
			if(StringUtils.isBlank(codResultado) || transaccionBienTerminada){
				String request = generateRequest(ANULACION, null);
				log.info("cancelReturn() - ANULACION DEVOLUCION - REQUEST CONEXFLOW : " + request);
				
				String response = sendRequest(request);
				log.info("cancelReturn() - ANULACION DEVOLUCION - RESPONSE CONEXFLOW : " + response);
			}
			
			PaymentOkEvent event = new PaymentOkEvent(this, payment.getPaymentId(), payment.getAmount());
			event.setCanceled(true);
			event.addExtendedData(PARAM_RESPONSE_TEF, datosRespuesta);
			getEventHandler().paymentOk(event);
		}
		catch(Exception e){
			
		}
		finally{
			try{
				log.info("cancelReturn() - CONFIRMACION - REQUEST CONEXFLOW : " + "O");
				sendRequest("O");
			}
			catch(Exception e){
				log.error("cancelReturn() - " + e.getMessage());
			}
			try{
				disconnect();
			}
			catch(Exception e){
				log.error("cancelReturn() - " + e.getMessage());
			}
		}
		
		return true;
	}
	
	/* ############################################################################################# */
	/* ########################################## TOTALES ########################################## */
	/* ############################################################################################# */
	
	public DatosRespuestaPagoTarjeta consultTotals(DatosPeticionPagoTarjeta requestData) throws PaymentException{
		log.info("consultTotals() - Iniciando pago en servicio VisualPlugin de Conexflow...");
		
		initialSetup();
		
		PaymentInitEvent initEvent = new PaymentInitEvent(this);
		getEventHandler().paymentInitProcess(initEvent);
		
		DatosRespuestaPagoTarjeta responseData = new DatosRespuestaPagoTarjeta(requestData);
		responseData.setTipoTransaccion(TOTALES);
		
		try{
			connect();
			
			String request = generateRequest(TOTALES, requestData);
			log.info("consultTotals() - TOTALES - REQUEST CONEXFLOW : " + request);
			
			String response = sendRequest(request);
			log.info("consultTotals() - TOTALES - RESPONSE CONEXFLOW : " + response);
			if(response.length() < 12){
				throw new RuntimeException(I18N.getTexto("Respuesta incorrecta del terminal de pago. Verifique que está preparado."));
			}
			
			saveResponse(responseData, response);
			processResponseTransactionPayment(responseData);
			
			transaccionBienTerminada = true;
		}
		catch(Exception e){
			String msgError = e.getMessage();
			log.error("consultTotals() - " + msgError, e);
			
			if(e instanceof SocketTimeoutException){
				msgError = I18N.getTexto("HA EXPIRADO EL TIEMPO DE ESPERA AL SERVIDOR DE CONEXFLOW");
			}
			else if(e.getMessage().equals(ERROR_CONNECT_REFUSED)){
				msgError = I18N.getTexto("NO HA SIDO POSIBLE ESTABLECER LA COMUNICACIÓN CON EL PINPAD. "
						+ "\n\n ASEGÚRESE DE QUE EL PINPAD ESTÁ CONECTADO Y EL SERVICIO VISUAL PLUGIN ENCENDIDO");
			}
			
			throw new PaymentException(e.getMessage(), e, paymentId, this);
		}
		finally{
			try{
				log.info("consultTotals() - CONFIRMACION - REQUEST CONEXFLOW : " + "O");
				sendRequest("O");
			}
			catch(Exception e){
				log.error("consultTotals() - " + e.getMessage());
			}
			try{
				disconnect();
			}
			catch(Exception e){
				log.error("consultTotals() - " + e.getMessage());
			}
		}
		
		return responseData;
	}
	
	/* ############################################################################################# */
	/* ########################################## COMÚNES ########################################## */
	/* ############################################################################################# */
	
	public void initialSetup() throws PaymentException{
		codResultado = null;
		transaccionBienTerminada = false;
		
		checkConfiguration();
	}
	
	@Override
	public void setConfiguration(PaymentMethodConfiguration configuration){
		super.setConfiguration(configuration);
		try{
			log.info("setConfiguration() - Realizando conexión de prueba para inicializar dispositivo.");
			connect();
		}
		catch(Exception e){
			log.error("setConfiguration() - Ha habido un error al realizar la conexión previa: " + e.getMessage(), e);
		}
	}

	@Override
	public List<ConfigurationPropertyDto> getConfigurationProperties(){
		List<ConfigurationPropertyDto> properties = new ArrayList<ConfigurationPropertyDto>();
		properties.add(new ConfigurationPropertyDto("comercio", I18N.getTexto("Comercio")));
		properties.addAll(super.getConfigurationProperties());
		return properties;
	}
	
	public void checkConfiguration() throws PaymentException{
		String msgError = "";
		if(!StringUtils.isNotBlank(empresa) || !StringUtils.isNotBlank(tienda) || !StringUtils.isNotBlank(numeroTPV) 
				|| !StringUtils.isNotBlank(servidorTCP) || !StringUtils.isNotBlank(puertoTCP) || timeout == null){
			msgError = "La pasarela de este medio de pago no está configurada correctamente.";
		}
		if(empresa.length() != 8){
			msgError = "La longitud del codigo de comercio (" + empresa.length() + ") no es correcta (8)";
		}
		if(tienda.length() != 4){
			msgError = "La longitud del codigo de tienda (" + tienda.length() + ") no es correcta (4)";
		}
		if(numeroTPV.length() != 4){
			msgError = "La longitud del codigo de TPV (" + numeroTPV.length() + ") no es correcta (4)";
		}
		
		if(StringUtils.isNotBlank(msgError)){
			log.error("checkConfiguration() - " + msgError);
			throw new PaymentException(msgError);
		}
	}
	
	public String createTransactionNumber(DatosPeticionPagoTarjeta datosPeticion){
		String transactionNumber = datosPeticion.getIdTransaccion().replaceAll("[a-zA-Z]", "").replaceAll("/", "");
		log.info("createTransactionNumber() - Número de transacción (EM) creado para la operación : " + transactionNumber);
		return transactionNumber;
	}
	
	public String generateRequest(String type, DatosPeticionPagoTarjeta datosPeticion){
		log.debug("generateRequest() - Iniciando la creación de la petición para el servicio VisualPlugin de Conexflow...");
		
		String request = "", importeTransaccion = "", idTicket = "";

		Date fechaActual = new Date();
		String fechaFormateada = formatFecha.format(fechaActual);
		String horaFormateada = formatHora.format(fechaActual);

		switch(type){
			case VENTA:
				request = REQUEST_CODE;
				
				/* Insertamos un identificador por delante para diferenciar ventas de devoluciones. */
				intTransactionID = "0" + intTransactionID;
				importeTransaccion = datosPeticion.getImporte().multiply(new BigDecimal(100)).toBigInteger().toString();
				idTicket = datosPeticion.getIdDocumento().toString();
				
				request = request  + "03" + generateRequestSegment("E0", empresa + tienda + numeroTPV)
				        + generateRequestSegment("E1", importeTransaccion)
				        + generateRequestSegment("E3", idTicket)
				        + generateRequestSegment("F3", fechaFormateada)
				        + generateRequestSegment("F4", horaFormateada)
						+ generateRequestSegment("EM", intTransactionID);
			break;
			case DEVOLUCION:
				request = REQUEST_CODE;
				
				/* Insertamos un identificador por delante para diferenciar ventas de devoluciones. */
				intTransactionID = "1" + intTransactionID;
				importeTransaccion = datosPeticion.getImporte().abs().multiply(new BigDecimal(100)).toBigInteger().toString();
				idTicket = datosPeticion.getIdDocumento().toString();
				
				request = request + "04" + generateRequestSegment("E0", empresa + tienda + numeroTPV)
				        + generateRequestSegment("E1", importeTransaccion)
				        + generateRequestSegment("E3", idTicket)
				        + generateRequestSegment("F3", fechaFormateada)
				        + generateRequestSegment("F4", horaFormateada)
						+ generateRequestSegment("EM", intTransactionID);
			break;
			case ANULACION:
				request = REQUEST_CODE;
				
				request = request + "05" + generateRequestSegment("E0", empresa + tienda + numeroTPV)
						+ generateRequestSegment("E1", importeTransaccion)
				        + generateRequestSegment("F3", fechaFormateada)
				        + generateRequestSegment("F4", horaFormateada)
						+ generateRequestSegment("EM", intTransactionID);
			break;
			case TOTALES:
				request = REQUEST_CODE_TOTAL;
				
				request = request + generateRequestSegment("E9", fechaFormateada);
			default:
			break;
		}	
		
		request = "P" + StringUtils.leftPad(Integer.toString(request.length()), 5, '0') + request;
		log.debug("generateRequest() - Trama generada: " + request);
		
		return request;
	}
	
	public String generateRequestSegment(String code, String valor){
		return code + StringUtils.leftPad(Integer.toString(valor.length()), 4, '0') + valor;
	}
	
	/*public String sendRequest(String request) throws IOException{
		log.info("sendRequest() - Iniciando el envío de la petición al servicio VisualPlugin de Conexflow...");
		
		byte buffer[] = new byte[50000];
		DataInputStream entrada = new DataInputStream(socket.getInputStream());
		DataOutputStream salida = new DataOutputStream(socket.getOutputStream());
		salida.write(request.getBytes());

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int s;
		do{
			s = entrada.read(buffer);
			if(s != -1){
				baos.write(buffer, baos.size(), s);
			}
		}
		while (s >= buffer.length);

		return new String(baos.toByteArray(), "UTF-8");
	}*/
	protected String sendRequest(String peticion) {
		log.debug("sendRequest() - Enviando el siguiente mensaje al servicio ConexFlow: " + peticion);

		DataInputStream in;
		DataOutputStream salida;

		try {
			in = new DataInputStream(socket.getInputStream());
			salida = new DataOutputStream(socket.getOutputStream());
	
			salida.write(peticion.getBytes());
	
			byte[] messageByte = new byte[BUFFER_LENGTH];
			boolean end = false;
			StringBuilder dataString = new StringBuilder(BUFFER_LENGTH);
			int totalBytesRead = 0;
			int totalBytesResponse = 0;
			while (!end) {
				int currentBytesRead = in.read(messageByte);
				if (currentBytesRead != -1) {
					totalBytesRead = currentBytesRead + totalBytesRead;
					if (totalBytesRead <= BUFFER_LENGTH) {
						dataString.append(new String(messageByte, 0, currentBytesRead, StandardCharsets.UTF_8));
					}
					else {
						dataString.append(new String(messageByte, 0, BUFFER_LENGTH - totalBytesRead + currentBytesRead, StandardCharsets.UTF_8));
					}
					
					if (dataString.length() >= BUFFER_LENGTH || (totalBytesRead >= totalBytesResponse && totalBytesResponse > 0)) {
						end = true;
					}
					
					// Comprobamos el fin de la trama según indicaciones de ConexFlow.
					// Del cracter al 2 al 6 de la respuesta viene la longitud de la trama, obviando
					// la cabecera que son 6 caracteres.
					if(StringUtils.isNotBlank(dataString.toString().trim())) {
						totalBytesResponse = new Integer(dataString.toString().substring(2, 6));
						if(totalBytesRead == totalBytesResponse + 6) {
							end = true;
						}
					}
				}
				else {
					end = true;
				}
			}
			
			return dataString.toString();
		} catch (SocketTimeoutException e) {
		   throw new RuntimeException(I18N.getTexto("sendRequest() - Tiempo máximo de espera alcanzado realizando la petición. Por favor, revise su conexión y vuelva a intentarlo."));	
		} catch (IOException e) {
			throw new RuntimeException(I18N.getTexto("sendRequest() - Error de Entrada/Salida al realizar la petición"));	
		}
	}
	
	public void saveResponse(DatosRespuestaPagoTarjeta responseData, String result) {
		log.debug("saveResponse() - Iniciando el guardado de la respuesta del servicio de VisualPlugin de Conexflow...");
		
		responseData.setMsgRespuesta(result);
		
		Map<String, String> segment = extractSegmentsResponse(result);
		
		responseData.setCodResult(segment.get("S0"));
		codResultado = responseData.getCodResult();
		
		responseData.setComercio(segment.get("S1"));
		responseData.setCodigoCentro(segment.get("S2"));
		responseData.setTerminalId(segment.get("SQ"));
		responseData.setPos(segment.get("S3"));
		responseData.setVerificacion(segment.get("S4"));
		responseData.setAuthMode(segment.get("S5"));
		
		String segmentoSP = segment.get("SP");
		String segmentoS8 = segment.get("S8");
		if(StringUtils.isNotBlank(segmentoSP) && StringUtils.isNotBlank(segmentoS8)) {
			String hora = segmentoSP.substring(0, 2) + ":" + segmentoSP.substring(2, 4) + ":" + segmentoSP.substring(4, 6);
			String fecha = segmentoS8.substring(6, 8) + "/" + segmentoS8.substring(4, 6) + "/" + segmentoS8.substring(0, 4);
			responseData.setFechaTransaccion(fecha + " " + hora);
		}
		
		responseData.setNumTransaccion(segment.get("S9"));
		
		String pan = segment.get("SC");
		if(StringUtils.isNotBlank(pan)) {
			pan = "************" + pan.substring(pan.length()-4, pan.length());
			responseData.setTarjeta(pan);
			responseData.setPAN(pan);
		}
		
		responseData.setTipoLectura(segment.get("SB"));
		responseData.setCodAutorizacion(segment.get("SI"));
		responseData.setDescBanco(segment.get("SO"));
		responseData.setAID(segment.get("R0"));
		responseData.setApplicationLabel(segment.get("R1"));
		responseData.setARC(segment.get("R2"));
		responseData.setFuc(segment.get("SR"));
		responseData.setNombredf(segment.get("R0"));
		
		Map<String, String> adicionales = new HashMap<String, String>();
		adicionales.put("EM", intTransactionID);
		adicionales.put(COMERCIO, empresa);
		adicionales.put(TIENDA, tienda);
		adicionales.put("TPVOrigen", numeroTPV);

		adicionales.put(NUM_VENTAS, segment.get("TG"));
		adicionales.put(IMPORTE_VENTAS, segment.get("TH"));
		adicionales.put(NUM_DEVOLUCIONES, segment.get("TI"));
		adicionales.put(IMPORTE_DEVOLUCIONES, segment.get("TJ"));
		Integer numAnulaciones = (segment.get("TK") != null ? Integer.valueOf(segment.get("TK")) : 0) + (segment.get("TM") != null ? Integer.valueOf(segment.get("TM")) : 0);
		Double impAnulaciones = (segment.get("TL") != null ? Double.valueOf(segment.get("TL")) : 0) + (segment.get("TN") != null ? Double.valueOf(segment.get("TN")) : 0);
		adicionales.put(NUM_ANULACIONES, numAnulaciones.toString());
		adicionales.put(IMPORTE_ANULACIONES, impAnulaciones.toString());
		
		responseData.setAdicionales(adicionales);
	}
	
	public Map<String, String> extractSegmentsResponse(String result){
		Map<String, String> segments = new HashMap<String, String>();
		String operationalResponse = result.substring(18, result.length());
		int index = 0;
		
		while(index <= operationalResponse.length() - 1){
			String segment = operationalResponse.substring(index, index + 2);
			Integer length = new Integer(operationalResponse.substring(index + 2, index + 6));
			String segmentValue = operationalResponse.substring(index + 6, index + 6 + length);
			segments.put(segment, segmentValue);
			index = index + 6 + length;
		}
		
		return segments;
	}
	
	public void processResponseTransactionPayment(DatosRespuestaPagoTarjeta responseData) throws TarjetaException, IOException, DispositivoException {
		log.debug("processResponseTransactionPayment() - Iniciando el procesado de la respuesta del servicio VisualPlugin de Conexflow...");
		
	    if(!responseData.getCodResult().equals("500") && !responseData.getCodResult().equals("000")) {
	    	processErrorResponse(responseData);
	    }
    }
	
	public void processErrorResponse(DatosRespuestaPagoTarjeta responseData) throws TarjetaException {
		log.debug("processErrorResponse() - Iniciando el procesado de la respuesta erronea del servicio VisualPlugin de Conexflow...");
		
		String msgError = I18N.getTexto("Cod. resultado: ") + responseData.getCodResult();
		if(errores.containsKey(responseData.getCodResult())) {
			msgError = msgError + System.lineSeparator() + System.lineSeparator() 
					+ I18N.getTexto(errores.get(responseData.getCodResult()))
					+ System.lineSeparator() + System.lineSeparator() ;
		}
		
		throw new TarjetaException(I18N.getTexto("Error al realizar la transacción. " + System.lineSeparator() + System.lineSeparator() + msgError), responseData);
	}
	
	public void setPaymentOkEvent(BigDecimal amount, DatosRespuestaPagoTarjeta datosRespuesta) {
		PaymentOkEvent event = new PaymentOkEvent(this, paymentId, amount);		
		event.addExtendedData(PARAM_RESPONSE_TEF, datosRespuesta);
		getEventHandler().paymentOk(event);
		
		log.info("setPaymentOkEvent() - EVENTO DE CONEXFLOW CONFIRMADO...");
	}
	
}
