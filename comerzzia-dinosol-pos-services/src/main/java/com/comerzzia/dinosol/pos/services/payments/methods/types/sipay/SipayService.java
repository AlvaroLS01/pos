package com.comerzzia.dinosol.pos.services.payments.methods.types.sipay;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.comerzzia.dinosol.librerias.sipay.client.SipayClient;
import com.comerzzia.dinosol.librerias.sipay.client.constants.SipayConstants;
import com.comerzzia.dinosol.librerias.sipay.client.wsdl.sipayservice.BeginDevolutionResponse;
import com.comerzzia.dinosol.librerias.sipay.client.wsdl.sipayservice.BeginSellTransactionResponse;
import com.comerzzia.dinosol.librerias.sipay.client.wsdl.sipayservice.CallSpecificFunctionResponse;
import com.comerzzia.dinosol.librerias.sipay.client.wsdl.sipayservice.GetNextMessageResponse;
import com.comerzzia.dinosol.librerias.sipay.client.wsdl.sipayservice.Header;
import com.comerzzia.dinosol.librerias.sipay.client.wsdl.sipayservice.InitializeDeviceResponse;
import com.comerzzia.dinosol.librerias.sipay.client.wsdl.sipayservice.PrintableData;
import com.comerzzia.dinosol.pos.services.core.variables.DinoVariablesService;
import com.comerzzia.dinosol.pos.services.payments.methods.types.sipay.exception.SipayException;
import com.comerzzia.dinosol.pos.services.ticket.cabecera.DinoCabeceraTicket;
import com.comerzzia.dinosol.pos.services.ticket.pagos.sipay.InfoSipayTransaction;
import com.comerzzia.pos.services.payments.PaymentDto;
import com.comerzzia.pos.services.payments.configuration.PaymentMethodConfiguration;
import com.comerzzia.pos.services.payments.configuration.PaymentsMethodsConfiguration;
import com.comerzzia.pos.services.ticket.ITicket;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.services.ticket.TicketsServiceException;
import com.comerzzia.pos.services.ticket.copiaSeguridad.CopiaSeguridadTicketService;
import com.comerzzia.pos.services.ticket.pagos.PagoTicket;
import com.comerzzia.pos.services.ticket.pagos.tarjeta.DatosRespuestaPagoTarjeta;
import com.comerzzia.pos.util.i18n.I18N;

@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class SipayService {

	private static final Logger log = Logger.getLogger(SipayService.class);
	
	@Autowired
	private PaymentsMethodsConfiguration paymentsMethodsConfiguration;
	@Autowired
	private DinoVariablesService variablesServices;
	@Autowired
	private CopiaSeguridadTicketService copiaSeguridadTicketService;
	
	private SipayClient sipayClient;
	private String clientId, boxes, url, storeId, posId, lang;
	private Long timeout;
	private Header header;
	private final Semaphore semaphore = new Semaphore(1);
	
	
	public void doPaymentRequest(BigDecimal amount, DatosRespuestaPagoTarjeta response, ITicket ticket) throws TimeoutException, Exception {
		log.info("doPaymentRequest() - Iniciando operación de venta...");
		log.debug("doPaymentRequest() - Datos que se enviaran a la peticion: ");
		log.debug("doPaymentRequest() - Amount: " + amount);
		log.debug("doPaymentRequest() - Codigo ticket: " + ticket.getCabecera().getCodTicket());
		BeginSellTransactionResponse responseSell = getSipayClient().beginSellTransaction(url, header, amount, ticket.getCabecera().getCodTicket());
		
		if (responseSell != null && responseSell.getResult().getCode() == 0) {		
			obtenerYGestionarRespuesta(response);
			
			if(response == null || StringUtils.isBlank(response.getNumOperacion())) {
				throw new SipayException(I18N.getTexto("No se pudo completar la operación. Por favor, inténtenlo de nuevo."));
			}
		} 
		else {
			throw new Exception (SipayConstants.ERROR_MESSAGE_NOT_SENT + (responseSell == null ? "" : responseSell.getResult().getDescription()));
		}
	}
	
	public void doReturnRequest(BigDecimal amount, DatosRespuestaPagoTarjeta response, ITicket ticket) throws TimeoutException, Exception {
		log.info("doReturnRequest() - Iniciando operación de devolución...");
		log.debug("doReturnRequest() - Datos que se enviaran a la peticion: ");
		log.debug("doReturnRequest() - Amount: " + amount);
		BeginDevolutionResponse responseDevolution = null;
		
		if(ticket.getCabecera().getDatosDocOrigen() != null && StringUtils.isNotBlank(ticket.getCabecera().getDatosDocOrigen().getCodTicket())) {
			log.debug("doReturnRequest() - Devolución referenciada");
			log.debug("doReturnRequest() - Codigo ticket origen: " + ticket.getCabecera().getDatosDocOrigen().getCodTicket());
			responseDevolution = getSipayClient().beginDevolution(url, header, amount, ticket.getCabecera().getDatosDocOrigen().getCodTicket());
		} else if(StringUtils.isNotBlank(ticket.getCabecera().getCodTicket())) {
			log.debug("doReturnRequest() - Devolución no referenciada");
			log.debug("doReturnRequest() - Codigo ticket: " + ticket.getCabecera().getCodTicket());
			responseDevolution = getSipayClient().beginDevolution(url, header, amount, ticket.getCabecera().getCodTicket());
		} else {
			String msgError = "Operación cancelada. No se ha encontrado el código del documento.";
			log.error("doReturnRequest() - " + msgError);
			throw new Exception (msgError);
		}
		
		if (responseDevolution != null && responseDevolution.getResult().getCode() == 0) {
			obtenerYGestionarRespuesta(response);
			
			if(response == null || StringUtils.isBlank(response.getNumOperacion())) {
				throw new SipayException(I18N.getTexto("No se pudo completar la operación. Por favor, inténtenlo de nuevo."));
			}
		} else {
			throw new Exception (SipayConstants.ERROR_MESSAGE_NOT_SENT + (responseDevolution == null ? "" : responseDevolution.getResult().getDescription()));
		}
	}
	
	public void doPaymentCancelRequest(PaymentDto payment, DatosRespuestaPagoTarjeta response, ITicket ticket) throws Exception {
		log.info("doPaymentCancelRequest()...");
		String sequence = "";
		if (payment.getExtendedData().get("PARAM_RESPONSE_TEF") != null) {
			DatosRespuestaPagoTarjeta datosPagoOrigen = (DatosRespuestaPagoTarjeta) payment.getExtendedData().get("PARAM_RESPONSE_TEF");
			if (StringUtils.isNotBlank(datosPagoOrigen.getNumOperacion())) {
				sequence = "sq:" + datosPagoOrigen.getNumOperacion();
			}
		}
		cancelarOperacion(sequence, getAmountOperation(payment.getPaymentId(), payment.getPaymentCode(), ticket), ticket);
	}
	
	public void setConfiguration(PaymentMethodConfiguration configuration) {
		
		clientId = configuration.getGatewayConfigurationProperty(SipayConstants.CLIENT_ID);
		boxes = configuration.getGatewayConfigurationProperty(SipayConstants.BOXES);
		String timeout = configuration.getGatewayConfigurationProperty(SipayConstants.TIMEOUT);
		
		if (StringUtils.isBlank(timeout)) {
			this.timeout = SipayConstants.TIMEOUT_DEFAULT_VALUE;
		} else {
			this.timeout = Long.parseLong(timeout);
		}
        
		storeId = configuration.getConfigurationProperty(SipayConstants.STORE_ID);
		posId = configuration.getConfigurationProperty(SipayConstants.POS_ID);
		lang = configuration.getConfigurationProperty(SipayConstants.LANGUAGE);
		
		url = configuration.getConfigurationProperty(SipayConstants.URL);
		
		header = getHeader();
		
		log.info("setConfiguration() - Configuración de Sipay: ");
		log.info("setConfiguration() - Id cliente: " + clientId);
		log.info("setConfiguration() - Cajones: " + boxes);
		log.info("setConfiguration() - URL: " + url);
		log.info("setConfiguration() - Timeout: " + timeout);
		log.info("setConfiguration() - Id tienda: " + storeId);
		log.info("setConfiguration() - Id POS: " + posId);
		log.info("setConfiguration() - Idioma: " + lang);
		
	}
	
	public Header getHeader() {
		Header header = new Header();
		header.setClientId(clientId);
		header.setStoreId(storeId);
		header.setPosId(posId);
		header.setLang(lang);
		header.setExtraData1("");
		return header;
	}
	
	public void conect() throws Exception { 
		log.info("conect()...");
		
		if (sipayClient == null) {
			sipayClient = new SipayClient();
		}
	}

	public void leerConfiguracion() throws Exception {
		
		for (PaymentMethodConfiguration configuration : paymentsMethodsConfiguration.getPaymentsMethodsConfiguration()) {
			if (StringUtils.isNotBlank(configuration.getControlClass()) && configuration.getControlClass().equals("tefSipayManager")) {
				clientId = configuration.getGatewayConfigurationProperty(SipayConstants.CLIENT_ID);
				String timeoutStr = configuration.getConfigurationProperty(SipayConstants.TIMEOUT);
				boxes = configuration.getGatewayConfigurationProperty(SipayConstants.BOXES);
				
				timeout = null;				
				if (StringUtils.isBlank(timeoutStr)) {
					timeout = 120000L;
				} else {
					timeout = Long.parseLong(timeoutStr);
				}
				
				storeId = configuration.getConfigurationProperty(SipayConstants.STORE_ID);
				posId = configuration.getConfigurationProperty(SipayConstants.POS_ID);
				lang = configuration.getConfigurationProperty(SipayConstants.LANGUAGE);
				url = configuration.getConfigurationProperty(SipayConstants.URL);
				header = getHeader();				
			}
		}
	}
	public void cargarClavesCajones() throws Exception {		
		conect();
		leerConfiguracion();
		// Obtenemos los cajones de la pasarela de pagos
		if (StringUtils.isNotBlank(boxes)) {
			inicializarDispositivo(false);
			
			// DIN-550. A petición de Sipay
			Thread.sleep(2000);
			
			log.info("cargarClavesCajones() - Actualizando claves para los cajones: " + boxes);
			String[] cajonesElegidos = boxes.split(",");
			
			// Actualizamos las claves para cada uno de los cajones
			for(String cajon : cajonesElegidos) {
		       if(StringUtils.isNotBlank(cajon.trim())) {
		    	   log.info("cargarClavesCajones() - Actualizando claves para el cajón: " + cajon);
		    	   CallSpecificFunctionResponse response = sipayClient.loadKeys(url, header, cajon.trim());
			   		if (response == null || (response != null && response.getResult().getCode() != 0)) {
						throw new Exception (SipayConstants.ERROR_MESSAGE_NOT_SENT + (response == null ? "" : response.getResult().getDescription()));
					} else if (response != null && response.getResult().getCode() == 0) {
						obtenerYGestionarRespuesta(null); 
					}
		       }
			}
			
		} else {
			throw new Exception("No hay cajones de claves configurados en la pasarela");
		}
	}
	
	public void inicializarDispositivo(boolean cargarConfiguracion) throws Exception {
		log.info("inicializarDispositivo() - Inicializando el dispositivo...");
		conect();
		
		if (cargarConfiguracion) {
			leerConfiguracion();
		}
		InitializeDeviceResponse responseinitialize = sipayClient.initializeDevice(url, header);
		if (responseinitialize == null || (responseinitialize != null && responseinitialize.getResult().getCode() != 0)) {
			throw new Exception (SipayConstants.ERROR_MESSAGE_NOT_SENT + (responseinitialize == null ? "" : responseinitialize.getResult().getDescription()));
		}
		
		log.info("inicializarDispositivo() - Dispositivo inicializado correctamente");
	}
	
	
	public void obtenerYGestionarRespuesta(DatosRespuestaPagoTarjeta responsePagoTarjeta) throws TimeoutException, Exception {
		try {
			GetNextMessageResponse responseNextMessage = consultarMensajesRespuesta();
			PrintableData printableData = interpretaMensaje(responsePagoTarjeta, responseNextMessage);
			
			if (printableData != null) {
				rellenarResponse(responsePagoTarjeta, printableData);
			}
			
		} catch (TimeoutException toe) {
			String msgError = "Tiempo de espera agotado, vuelva a realizar la operación";
			log.error(msgError);
			throw new TimeoutException(I18N.getTexto(msgError));
		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		}
	}
	
	private GetNextMessageResponse consultarMensajesRespuesta() throws InterruptedException, ExecutionException, TimeoutException {
	    log.info("consultarMensajesRespuesta() - Consultando respuestas...");

	    if (!semaphore.tryAcquire()) {
	        throw new IllegalStateException("Ya existe una operación en activo, finalice para continuar.");
	    }

	    try {
	        GetNextMessageResponse responseNextMessage = null;
	        ExecutorService executor = Executors.newSingleThreadExecutor();

	        Future<GetNextMessageResponse> future = executor.submit(() -> {
	            GetNextMessageResponse respNextMes = null;
	            try {
	                do {
	                    Thread.sleep(300);
	                    respNextMes = sipayClient.getNextMessage(url, header);
	                    log.debug("consultarMensajesRespuesta() - " + respNextMes.toString());
	                } while (!Thread.currentThread().isInterrupted() && respNextMes != null &&
	                        respNextMes.getMessage() != null && respNextMes.getMessage().getMessageCode() == 10);
	            } catch (InterruptedException e) {
	                Thread.currentThread().interrupt();
	            }
	            return respNextMes;
	        });

	        try {
	            responseNextMessage = future.get(timeout, TimeUnit.MILLISECONDS);
	        } catch (TimeoutException toe) {
	            future.cancel(true);
	            throw new TimeoutException(toe.getMessage());
	        } finally {
	            executor.shutdown();
	            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
	                executor.shutdownNow();
	            }
	        }

	        return responseNextMessage;
	    } finally {
	        semaphore.release(); // Libera el acceso para permitir que otro hilo entre.
	    }
	}
	
	private PrintableData interpretaMensaje(DatosRespuestaPagoTarjeta responsePagoTarjeta, GetNextMessageResponse responseNextMessage)
			throws InterruptedException, ExecutionException, TimeoutException, Exception {
		log.info("interpretaMensaje() - Interpretando mensaje de respuesta...");
		PrintableData printableData = null;
		
		if (responseNextMessage != null && responseNextMessage.getMessage() != null) {
			int msgCode = responseNextMessage.getMessage().getMessageCode();
			log.info("interpretaMensaje() - Mensaje recibido: " + msgCode + " - " + responseNextMessage.getMessage().getMessageText());
			
			if (msgCode == 11) { // Mensajes que muestran el siguiente paso para continuar con la operación
				obtenerYGestionarRespuesta(responsePagoTarjeta);
			} else if (msgCode == 1000) { // La operación ha sido aceptada y autorizada
				log.info("interpretaMensaje() - Operación completada con éxito");
				if (responseNextMessage.getMessage().getPrintableData() != null) {
					printableData = responseNextMessage.getMessage().getPrintableData().getValue();
					log.debug("interpretaMensaje() - Amount recibido: " + printableData.getAmount());
				}
			} else if (msgCode == 1001) { // La operación no ha finalizado correctamente
				log.info("interpretaMensaje() - Error en la operación");
				throw new Exception(responseNextMessage.getMessage().getMessageText());
			}
		}
		
		return printableData;
	}
	
	public void rellenarResponse(DatosRespuestaPagoTarjeta response, PrintableData printableData) {
		if (response != null && printableData != null) {
			response.setTipoOp(printableData.getOperationType()); // Obligatorio
			response.setARC(printableData.getACRCode()); // Obligatorio si la operación se ha realizado con tarjeta EMV
			response.setFuc(printableData.getFUCCode()); // Ya no se muestra nunca por seguridad
			response.setImporte(printableData.getAmount()); // Obligatorio
			response.setAID(printableData.getAID()); // Obligatorio si la operación se ha realizado con tarjeta EMV
			response.setTarjeta(printableData.getCardNumber()); // Obligatorio
			response.setMarcaTarjeta(printableData.getAppLabel()); // Obligatorio solo si la operación se ha realizado con tarjeta EMV 'ApplicationLabel(T50), 'Etiqueta del terminal'
			response.setDescBanco(printableData.getHCP());
			response.setNumOperacionBanco(printableData.getSequenceNumber());
			response.setCodAutorizacion(printableData.getAuthorizationNumber()); // Obligatorio
			response.setMoneda(printableData.getCurrencySimbol()); // Obligatorio
			response.setNumOperacion(printableData.getSequenceNumber());
			response.setVerificacion(printableData.getDataVerificationType()); // Obligatorio P, F, PF, *
			response.setTipoTransaccion(printableData.getOperationType());
			response.setNombreEntidad(printableData.getHCP());
			response.setPAN(printableData.getCardNumber());
			response.setApplicationLabel(printableData.getTerminalLabel());
			response.setFechaTransaccion(printableData.getDateTime());
			response.setAuthMode(printableData.getAuthorizationMode());
			response.setContactLess(printableData.getContactlessLiteral());
			response.setPedirFirma("F".equals(printableData.getDataVerificationType()) || "PF".equals(printableData.getDataVerificationType()) ? true : false);
			response.setNombredf(printableData.getDDFName());
			
			String hora = "", fecha = "";
			//En el modo offline el patron es diferente a como llega en online, controlamos que si no es uno es otro
			if (StringUtils.isNotBlank(printableData.getDateTime())) {
				try {
					String fechaHora = printableData.getDateTime();
					fecha = fechaHora.substring(0, 10);
					hora = fechaHora.substring(13, 18);
					log.info("rellenarResponse() - Fomato de la fecha hora recibida: "+fechaHora);
				}
				catch (Exception e) {
					String fechaHora = printableData.getDateTime();
					fecha = fechaHora.substring(0, 8);
					hora = fechaHora.substring(9, 16);
					log.error("rellenarResponse() - La fecha llega diferente en la respuesta, y usamos el otro patron para conseguir la fecha y hora.");
					log.info("rellenarResponse() - Fomato de la fecha hora recibida: "+fechaHora);
				}
			}
			
			response.setFechaBoleta(fecha); // Obligatorio
			response.setHoraBoleta(hora); // Obligatorio
			
			response.setAdicionales(getExtraData(printableData));
			
			if(response.getAdicional("exchangeRate") != null && StringUtils.isNotBlank(response.getAdicional("exchangeRate"))) {
				response.setDCC(true);
			}
		}
	}
	public Map<String, String> getExtraData(PrintableData printableData) {
		Map<String, String> adicionales = new HashMap<String, String>();
		
		try {
			String extraData = StringEscapeUtils.unescapeXml(printableData.getExtraData1());
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource src = new InputSource();
			src.setCharacterStream(new StringReader(extraData));

			Document doc = builder.parse(src);
			
			// <DCC>
			String exchangeRate = doc.getElementsByTagName("EXCHANGE_RATE").item(0) != null 
			    ? doc.getElementsByTagName("EXCHANGE_RATE").item(0).getTextContent() 
			    : "";
			String markUp = doc.getElementsByTagName("MARK_UP").item(0) != null 
			    ? doc.getElementsByTagName("MARK_UP").item(0).getTextContent() 
			    : "";
			String commision = doc.getElementsByTagName("COMMISION").item(0) != null 
			    ? doc.getElementsByTagName("COMMISION").item(0).getTextContent() 
			    : "";
			String percentMarginExchangeBce = doc.getElementsByTagName("PERCENT_MARGIN_EXCHANGE_BCE").item(0) != null 
			    ? doc.getElementsByTagName("PERCENT_MARGIN_EXCHANGE_BCE").item(0).getTextContent() 
			    : "";
			String exchangeRateBce = doc.getElementsByTagName("EXCHANGE_RATE_BCE").item(0) != null 
			    ? doc.getElementsByTagName("EXCHANGE_RATE_BCE").item(0).getTextContent() 
			    : "";
			String dccConversionFactor = doc.getElementsByTagName("DCC_CONVERSION_FACTOR").item(0) != null 
			    ? doc.getElementsByTagName("DCC_CONVERSION_FACTOR").item(0).getTextContent() 
			    : "";
			String dccMerchant = doc.getElementsByTagName("DCC_MERCHANT").item(0) != null 
			    ? doc.getElementsByTagName("DCC_MERCHANT").item(0).getTextContent() 
			    : "";
			String dccAmount = doc.getElementsByTagName("DCC_AMOUNT").item(0) != null 
			    ? doc.getElementsByTagName("DCC_AMOUNT").item(0).getTextContent() 
			    : "";
			String dccCurrencyCode = doc.getElementsByTagName("DCC_CURRENCY_CODE").item(0) != null 
			    ? doc.getElementsByTagName("DCC_CURRENCY_CODE").item(0).getTextContent() 
			    : "";
			// </DCC>

			// <COMMERECE>
			String ident = doc.getElementsByTagName("IDENT").item(0) != null 
			    ? doc.getElementsByTagName("IDENT").item(0).getTextContent() 
			    : "";
			String address = doc.getElementsByTagName("ADDRESS").item(0) != null 
			    ? doc.getElementsByTagName("ADDRESS").item(0).getTextContent() 
			    : "";
			String tel = doc.getElementsByTagName("TEL").item(0) != null 
			    ? doc.getElementsByTagName("TEL").item(0).getTextContent() 
			    : "";
			String store = doc.getElementsByTagName("STORE").item(0) != null 
			    ? doc.getElementsByTagName("STORE").item(0).getTextContent() 
			    : "";
			String city = doc.getElementsByTagName("CITY").item(0) != null 
			    ? doc.getElementsByTagName("CITY").item(0).getTextContent() 
			    : "";
			String cif = doc.getElementsByTagName("CIF").item(0) != null 
			    ? doc.getElementsByTagName("CIF").item(0).getTextContent() 
			    : "";
			String ticket = doc.getElementsByTagName("TICKET").item(0) != null 
			    ? doc.getElementsByTagName("TICKET").item(0).getTextContent() 
			    : "";
			// </COMMERECE>

			// <CASHBACK>
			String commissionType = doc.getElementsByTagName("COMMISSION_TYPE").item(0) != null 
			    ? doc.getElementsByTagName("COMMISSION_TYPE").item(0).getTextContent() 
			    : "";
			String commission = doc.getElementsByTagName("COMMISSION").item(0) != null 
			    ? doc.getElementsByTagName("COMMISSION").item(0).getTextContent() 
			    : "";
			String currency = doc.getElementsByTagName("CURRENCY").item(0) != null 
			    ? doc.getElementsByTagName("CURRENCY").item(0).getTextContent() 
			    : "";
			String additionalCommissionType = doc.getElementsByTagName("ADDITIONAL_COMMISSION_TYPE").item(0) != null 
			    ? doc.getElementsByTagName("ADDITIONAL_COMMISSION_TYPE").item(0).getTextContent() 
			    : "";
			String additionalComission = doc.getElementsByTagName("ADDITIONAL_COMMISSION").item(0) != null 
			    ? doc.getElementsByTagName("ADDITIONAL_COMMISSION").item(0).getTextContent() 
			    : "";
			// </CASHBACK>

			// <CARD_INFO>
			String bin = doc.getElementsByTagName("BIN").item(0) != null 
			    ? doc.getElementsByTagName("BIN").item(0).getTextContent() 
			    : "";
			String familyId = doc.getElementsByTagName("FAMILY_ID").item(0) != null 
			    ? doc.getElementsByTagName("FAMILY_ID").item(0).getTextContent() 
			    : "";
			String familyName = doc.getElementsByTagName("FAMILY_NAME").item(0) != null 
			    ? doc.getElementsByTagName("FAMILY_NAME").item(0).getTextContent() 
			    : "";
			String emitBankCode = doc.getElementsByTagName("EMIT_BANK_CODE").item(0) != null 
			    ? doc.getElementsByTagName("EMIT_BANK_CODE").item(0).getTextContent() 
			    : "";
			String emitBankName = doc.getElementsByTagName("EMIT_BANK_NAME").item(0) != null 
			    ? doc.getElementsByTagName("EMIT_BANK_NAME").item(0).getTextContent() 
			    : "";
			String adqBankCode = doc.getElementsByTagName("ADQ_BANK_CODE").item(0) != null 
			    ? doc.getElementsByTagName("ADQ_BANK_CODE").item(0).getTextContent() 
			    : "";
			String adqBankName = doc.getElementsByTagName("ADQ_BANK_NAME").item(0) != null 
			    ? doc.getElementsByTagName("ADQ_BANK_NAME").item(0).getTextContent() 
			    : "";
			String bankNetwork = doc.getElementsByTagName("BANK_NETWORK").item(0) != null 
			    ? doc.getElementsByTagName("BANK_NETWORK").item(0).getTextContent() 
			    : "";
			String functionId = doc.getElementsByTagName("FUNCTION_ID").item(0) != null 
			    ? doc.getElementsByTagName("FUNCTION_ID").item(0).getTextContent() 
			    : "";
			String functionName = doc.getElementsByTagName("FUNCTION_NAME").item(0) != null 
			    ? doc.getElementsByTagName("FUNCTION_NAME").item(0).getTextContent() 
			    : "";
			// </CARD_INFO>

			// <PRIVATE_CARD>
			String infoType = doc.getElementsByTagName("INFO_TYPE").item(0) != null 
			    ? doc.getElementsByTagName("INFO_TYPE").item(0).getTextContent() 
			    : "";
			String reference = doc.getElementsByTagName("REFERENCE").item(0) != null 
			    ? doc.getElementsByTagName("REFERENCE").item(0).getTextContent() 
			    : "";
			String destiny = doc.getElementsByTagName("DESTINY").item(0) != null 
			    ? doc.getElementsByTagName("DESTINY").item(0).getTextContent() 
			    : "";
			String letter = doc.getElementsByTagName("LETTER").item(0) != null 
			    ? doc.getElementsByTagName("LETTER").item(0).getTextContent() 
			    : "";
			String paymentInfo = doc.getElementsByTagName("PAYMENT_INFO").item(0) != null 
			    ? doc.getElementsByTagName("PAYMENT_INFO").item(0).getTextContent() 
			    : "";
			// </PRIVATE_CARD>

			// OTROS
			String dataInputType = printableData.getDataInputType() != null 
			    ? printableData.getDataInputType() 
			    : "";
			String storePD = printableData.getStore() != null 
			    ? printableData.getStore() 
			    : "";
			String cityPD = printableData.getCity() != null 
			    ? printableData.getCity() 
			    : "";
			String cardHolder = printableData.getCardHolder() != null 
			    ? printableData.getCardHolder() 
			    : "";
			String terminalSequence = printableData.getTerminalSequence() != null 
			    ? printableData.getTerminalSequence() 
			    : "";
			String infoText = printableData.getInfoText() != null 
			    ? printableData.getInfoText() 
			    : "";
			String aclaratory = printableData.getAclaratory() != null 
			    ? printableData.getAclaratory() 
			    : "";

			
			// rellenamos el mapa con los datos adicionales
			adicionales.put("exchangeRate", exchangeRate);
			adicionales.put("markUp", markUp);
			adicionales.put("commision", commision);
			adicionales.put("percentMarginExchangeBce", percentMarginExchangeBce);
			adicionales.put("exchangeRateBce", exchangeRateBce);
			adicionales.put("dccConversionFactor", dccConversionFactor);
			adicionales.put("dccMerchant", dccMerchant);
			adicionales.put("dccAmount", dccAmount);
			adicionales.put("dccCurrencyCode", dccCurrencyCode);
			adicionales.put("ident", ident);
			adicionales.put("address", address);
			adicionales.put("tel", tel);
			adicionales.put("store", store);
			adicionales.put("city", city);
			adicionales.put("cif", cif);
			adicionales.put("ticket_sipay", ticket);
			adicionales.put("commissionType", commissionType);
			adicionales.put("commission", commission);
			adicionales.put("currency", currency);
			adicionales.put("additionalCommissionType", additionalCommissionType);
			adicionales.put("additionalComission", additionalComission);
			adicionales.put("bin", bin);
			adicionales.put("familyId", familyId);
			adicionales.put("familyName", familyName);
			adicionales.put("emitBankCode", emitBankCode);
			adicionales.put("emitBankName", emitBankName);
			adicionales.put("adqBankCode", adqBankCode);
			adicionales.put("adqBankName", adqBankName);
			adicionales.put("bankNetwork", bankNetwork);
			adicionales.put("functionId", functionId);
			adicionales.put("functionName", functionName);
			adicionales.put("infoType", infoType);
			adicionales.put("reference", reference);
			adicionales.put("destiny", destiny);
			adicionales.put("letter", letter);
			adicionales.put("paymentInfo", paymentInfo);
			adicionales.put("dataInputType", dataInputType);
			adicionales.put("storePD", storePD);
			adicionales.put("cityPD", cityPD);
			adicionales.put("cardHolder", cardHolder);
			adicionales.put("terminalSequence", terminalSequence);
			adicionales.put("infoText", infoText);
			adicionales.put("aclaratory", aclaratory);
			adicionales.put("sipay", "sipay");
			
		} catch (ParserConfigurationException | SAXException | IOException e) {
			log.error("No se han podido cargar los datos adicionales en la respuesta" + e.getMessage(), e);
		}
		
		return adicionales;
	}

	
	public SipayClient getSipayClient() {
		return sipayClient;
	}

	public void cambiarModoOfflineSipay(String valor) throws Exception {
		log.debug("cambiarModoOfflineSipay() - Cambiando modo operativa SiPay...");
		conect();
		if(url == null || header == null) {
			leerConfiguracion();
		}
		CallSpecificFunctionResponse response = getSipayClient().callSpecificFunction(url, header, SipayConstants.FUNCTION_OFFLINE, SipayConstants.MODIFIER_GENERIC, valor, null, null);
		if (response != null && response.getResult().getCode() == 0) {
			obtenerYGestionarRespuesta(null);
		}
	}
	
	public void keepAlive() {
	    log.debug("keepAlive() - Eliminando posibles operaciones en cola...");
	    int numIntentos = 2;
	    int contadorIntentos = 0;

	    while (contadorIntentos < numIntentos) {
	        try {
	            contadorIntentos++;
	            CallSpecificFunctionResponse response = getSipayClient().callSpecificFunction(url, header, SipayConstants.FUNCTION_KEEP_ALIVE, SipayConstants.MODIFIER_GENERIC, null, null, null);
	            if (response != null && response.getResult().getCode() == 0) {
	                obtenerYGestionarRespuesta(null);
	                break; 
	            }
	        } catch (Exception e) {
	            log.error("keepAlive() - Error al intentar eliminar operaciones en cola pendientes. Intento " + contadorIntentos + " de " + numIntentos + ". " + e.getMessage(),e);
	        }
	    }
	}
	
	public void cancelPayPendingTicket(int paymentId, String paymentCode, ITicket ticket) throws Exception {
		if (((DinoCabeceraTicket)ticket.getCabecera()).getTransactionsSipay() == null) {
			return;
		}
		if (lastTransactionHasBeenCompleted((DinoCabeceraTicket)ticket.getCabecera(), ticket.getPagos())) {
			return;
		}
		cancelPendingOperation(paymentId, paymentCode, ticket);
	}

	public boolean lastTransactionHasBeenCompleted(DinoCabeceraTicket dinoCabeceraTicket, List<PagoTicket> pagos) {
		log.debug("lastTransactionHasBeenCompleted () - Comprobando si existe transacción confirmada y pagos.");
		if (pagos == null || pagos.isEmpty()) {
			log.debug("lastTransactionHasBeenCompleted () - No existen pagos en el ticket. Se cancela operación registrada.");
			return false;
		}
		for (PagoTicket pagoTicket : pagos) {
			if (pagoTicket.getPaymentId().equals(dinoCabeceraTicket.getTransactionsSipay().getPaymentId())) {
				log.debug("lastTransactionHasBeenCompleted () - Existe transacción confirmada. No se cancela la operación registrada.");
				return true;
			}
		}
		return false;
	}
	
	public void cancelPendingOperation(int paymentId, String paymentCode, ITicket ticket) throws Exception {
		conect();
		keepAlive();
		log.debug("cancelarPagoTicketPendiente() - Existe una operación pendiente sin finalizar: CodTicket " + ((DinoCabeceraTicket)ticket.getCabecera()).getTransactionsSipay().getCodTicket() + " Amount = "
		        + ((DinoCabeceraTicket)ticket.getCabecera()).getTransactionsSipay().getAmount());

		cancelarOperacion(((DinoCabeceraTicket)ticket.getCabecera()).getTransactionsSipay().getCodTicket(), getAmountOperation(paymentId, paymentCode, ticket), ticket);
		log.debug("cancelarPagoTicketPendiente() - Operación pendiente sin finalizar cancelada correctamente. CodTicket " + ((DinoCabeceraTicket)ticket.getCabecera()).getTransactionsSipay().getCodTicket() + " Amount = "
		        + ((DinoCabeceraTicket)ticket.getCabecera()).getTransactionsSipay().getAmount());
	}

	
	public BigDecimal getAmountOperation(int paymentId, String paymentCode, ITicket ticket) {
		//Primero validar que no haya pendiente y cogemos su amount
		BigDecimal amount = null;
		if(((DinoCabeceraTicket)ticket.getCabecera()).getTransactionsSipay() != null) {
			amount = ((DinoCabeceraTicket)ticket.getCabecera()).getTransactionsSipay().getAmount();
		}
		//Luego comprobar los pago y si está usamos el importe del medio pago
		for (PagoTicket pago : (List<PagoTicket>) ticket.getPagos()) {
				if (paymentId == (pago.getPaymentId()) && paymentCode.equals(pago.getCodMedioPago())) {
					amount = pago.getImporte();
					break;
				}
		}
		return amount;
	}
	
	public void funcionesCargaFicheros(String rutaFichero, int funcion) throws Exception {		
		if (StringUtils.isNotBlank(rutaFichero)) {
			log.info("Se va a proceder la carga del fichero con ruta: " + rutaFichero);
			
			conect();
			leerConfiguracion();
			
			CallSpecificFunctionResponse response = getSipayClient().loadConfigurationFiles(url, header, funcion, rutaFichero);
			if (response != null && response.getResult().getCode() == 0) {
				obtenerYGestionarRespuesta(null);  
			} else {
				throw new Exception (SipayConstants.ERROR_MESSAGE_NOT_SENT + (response == null ? "" : response.getResult().getDescription()));
			}
		}
	}
	
	public void cargarFicheroTresMil (String rutaFichero) throws Exception {
		log.info("cargaFicheroTresMil() - Cargando fichero 3000...");
		funcionesCargaFicheros(rutaFichero, SipayConstants.FUNCTION_FILE3000);
	}

	public void realizarTelecargaVerifone (String rutaFichero) throws Exception {
		log.info("realizarTelecargaVerifone() - Realizando telecarga Verifone...");
		rutaFichero = "<TFILES><TFILE>" + rutaFichero + "</TFILE></TFILES>";
		funcionesCargaFicheros(rutaFichero, SipayConstants.FUNCTION_TELECARGA_VERIFONE);
	}
	
	public void cancelarOperacion(String sequence, BigDecimal amount, ITicket ticket) throws Exception {
		log.info("cancelarOperacion() - Cancelando operación...");
		conect();
		if (variablesServices.getVariableAsBoolean(ServicioConfiguracionSipay.X_POS_SIPAY_MODO_OFFLINE)) {
			throw new Exception("El medio de pago no puede ser anulado. Modo offline ACTIVADO");
		}

		if (StringUtils.isBlank(sequence)) {
			sequence = ticket.getCabecera().getCodTicket();
			if (ticket.isEsDevolucion() && ticket.getCabecera().getDatosDocOrigen() != null && StringUtils.isNotBlank(ticket.getCabecera().getDatosDocOrigen().getCodTicket())) {
				sequence = ticket.getCabecera().getDatosDocOrigen().getCodTicket();
			}
		}

		CallSpecificFunctionResponse response = getSipayClient().cancelOperation(url, header, amount, sequence);
		if (response != null && (response != null && response.getResult().getCode() == 0)) {
			obtenerYGestionarRespuesta(null);
		}
		else {
			throw new Exception(SipayConstants.ERROR_MESSAGE_NOT_SENT + (response == null ? "" : response.getResult().getDescription()));
		}
		log.info("La operación se ha cancelado correctamente");
	}
	
	public void completarResponse (DatosRespuestaPagoTarjeta response, ITicket ticket) {
		response.setComercio(clientId);
		response.setTerminal(posId); // Obligatorio
		response.setEmpleado(ticket.getCabecera().getCajero().getIdUsuario().toString());
	}
	
	public void keepTransactionSipay(BigDecimal amount, ITicket ticket, int paymentId) throws TicketsServiceException {
		log.debug("keepTransactionSipay() - Se va a guardar la copia de seguridad del ticket con código " + ticket.getCabecera().getCodTicket());
		InfoSipayTransaction infoTransaction = new InfoSipayTransaction(ticket.getCabecera().getCodTicket(), amount, paymentId);
		((DinoCabeceraTicket) ticket.getCabecera()).setTransactionsSipay(infoTransaction);
		copiaSeguridadTicketService.guardarBackupTicketActivo((TicketVentaAbono) ticket);
	}
	
	public void clearTransactionSipay(ITicket ticket) {
		((DinoCabeceraTicket) ticket.getCabecera()).setTransactionsSipay(null);
	}
}
