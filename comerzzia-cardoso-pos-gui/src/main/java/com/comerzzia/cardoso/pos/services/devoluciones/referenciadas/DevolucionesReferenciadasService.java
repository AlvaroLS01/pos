package com.comerzzia.cardoso.pos.services.devoluciones.referenciadas;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.net.ssl.SSLContext;

import org.apache.commons.lang.StringUtils;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.comerzzia.cardoso.pos.persistence.auditorias.AuditoriaDTO;
import com.comerzzia.cardoso.pos.persistence.auditorias.AuditoriaXML;
import com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas.AmountOfMoney;
import com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas.EmvData;
import com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas.approve.request.ApproveRequest;
import com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas.approve.response.ApproveResponse;
import com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas.approve.response.error.ApproveErrorException;
import com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas.approve.response.error.ApproveErrorResponse;
import com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas.approve.response.error.Error;
import com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas.cancel.request.CancelRequest;
import com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas.cancel.response.CancelResponse;
import com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas.cancel.response.error.CancelErrorResponse;
import com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas.refund.request.RefundPaymentSpecificInput;
import com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas.refund.request.RefundRequest;
import com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas.refund.request.RequestDTO;
import com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas.refund.response.RefundResponse;
import com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas.refund.response.error.RefundErrorException;
import com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas.refund.response.error.RefundErrorResponse;
import com.comerzzia.cardoso.pos.services.devoluciones.referenciadas.cancel.response.error.CancelErrorException;
import com.comerzzia.cardoso.pos.util.CardosoVariables;
import com.comerzzia.core.servicios.variables.VariablesService;
import com.comerzzia.pos.persistence.core.contadores.ContadorBean;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.persistence.paises.PaisBean;
import com.comerzzia.pos.persistence.tickets.POSTicketMapper;
import com.comerzzia.pos.persistence.tickets.TicketBean;
import com.comerzzia.pos.services.core.contadores.ServicioContadores;
import com.comerzzia.pos.services.core.paises.PaisService;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.util.i18n.I18N;
import com.comerzzia.pos.util.xml.MarshallUtil;
import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

@Service
public class DevolucionesReferenciadasService {

	protected static final Logger log = Logger.getLogger(DevolucionesReferenciadasService.class);

	@Autowired
	protected ResourceLoader resourceLoader;

	@Autowired
	protected Sesion sesion;

	@Autowired
	protected VariablesService variablesService;

	@Autowired
	protected ServicioContadores contadoresService;

	@Autowired
	protected PaisService paisService;

	@Autowired
	protected POSTicketMapper ticketMapper;

	public ApproveResponse devolucionApproveRefund(RequestDTO refundRequestDto) throws Exception {
		String url = "";
		
		url = refundRequestDto.getUrlHost() + "/paymentservice/globalmerchants/" + refundRequestDto.getGlobalMerchants() + "/pos/" + refundRequestDto.getPos() + "/payments/" + refundRequestDto.getPayments() + "/refund";
		
		log.debug("devolucionApproveRefund() - La url a la que se realiza la llamada " + url);
		String desCert = variablesService.consultarDefinicion(CardosoVariables.CRT_DES_WORLDLINE);
		File cert = resourceLoader.getResource(desCert).getFile();
		String certPassword = variablesService.consultarDefinicion(CardosoVariables.CRT_PASS_WORLDLINE);
		RefundResponse responseRefund = null;
		HttpResponse<String> response = null;
		// Configuración del certificado
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		FileInputStream inputStream = new FileInputStream(cert);
		keyStore.load(inputStream, certPassword.toCharArray());

		SSLContext sslContext = SSLContextBuilder.create().loadKeyMaterial(keyStore, certPassword.toCharArray()).build();

		// Configurar Unirest para usar el cliente HTTP personalizado
		Unirest.setHttpClient(HttpClients.custom().setSSLContext(sslContext).build());
		// Crear objeto JSON para el cuerpo de la solicitud
		RefundRequest requestBody = new RefundRequest();
		RefundPaymentSpecificInput refundPayment = new RefundPaymentSpecificInput();
		AmountOfMoney amountMoney = new AmountOfMoney();
		amountMoney.setAmount(refundRequestDto.getAmount());

		String codPais = sesion.getAplicacion().getTienda().getCliente().getCodpais();
		PaisBean pais = paisService.consultarCodPais(codPais);
		amountMoney.setCurrencyCode(pais.getCodDivisa());

		refundPayment.setAmountOfMoney(amountMoney);
		refundPayment.setComment(StringUtils.isBlank(refundRequestDto.getComment()) ? " " : refundRequestDto.getComment());
		Date fechaActual = new Date();
		// Formatear la fecha y hora actual a un String con minutos y segundos
		SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String fechaFormateada = formato.format(fechaActual);
		refundPayment.setMerchantReference(fechaFormateada);
		requestBody.setRefundPaymentSpecificInput(refundPayment);
		Gson gson = new Gson();
		String jsonBody = gson.toJson(requestBody);
		
		log.debug("devolucionApproveRefund() - El body de la llamada de refund: " + jsonBody);

		// Realizar la solicitud POST con Unirest
		response = Unirest.post(url).header("Content-Type", "application/json").body(jsonBody).asString();

		log.debug("devolucionApproveRefund() - La respuesta de la llamada es la siguiente: " + response.getStatus() + " - " +  response.getBody());
		int status = response.getStatus();

		if (status > 299) {
			RefundErrorResponse responseError = gson.fromJson(response.getBody(), RefundErrorResponse.class);
			String errores = "";
			if(responseError != null) {
				log.debug("devolucionApproveRefund() - Errores refund: " +responseError.toString());
				for (com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas.refund.response.error.Error error : responseError.getErrors()) {
					errores = error.getCode() + ": " + error.getMessage() + "; ";
				}
				throw new RefundErrorException(errores);
			}
		}
		else {
			responseRefund = gson.fromJson(response.getBody(), RefundResponse.class);
		}
		
		String urlApprove = "";
		
		urlApprove = refundRequestDto.getUrlHost() + "/paymentservice/globalmerchants/" + refundRequestDto.getGlobalMerchants() + "/pos/" + refundRequestDto.getPos() + "/payments/" + responseRefund.getPayment().getId() + "/approve";
		
		
		log.debug("devolucionApproveRefund() - La url a la que se realiza la llamada " + urlApprove);
		ApproveResponse approveRefund = null;
		
		ApproveRequest approveRequest = new ApproveRequest();
		EmvData emvData = responseRefund.getPayment().getPaymentOutput().getCardPaymentMethodSpecificOutput().getEmvData();
		approveRequest.setEmvData(emvData);
		approveRequest.setAmount(responseRefund.getPayment().getPaymentOutput().getAmountOfMoney().getAmount());
		approveRequest.setPaymentProductId(responseRefund.getPayment().getPaymentOutput().getCardPaymentMethodSpecificOutput().getPaymentProductId());

		String jsonBodyApprove = gson.toJson(approveRequest);
		
		log.debug("devolucionApproveRefund() - El body de la llamada del approve: " + jsonBodyApprove);

		response = Unirest.post(urlApprove).header("Content-Type", "application/json").body(jsonBodyApprove).asString();
		
		log.debug("devolucionApproveRefund() - La respuesta de la llamada es la siguiente: " + response.getStatus() + " - " +  response.getBody());
		
		int statusApprove = response.getStatus();

		if (statusApprove > 299) {
			ApproveErrorResponse responseError = gson.fromJson(response.getBody(), ApproveErrorResponse.class);
			String errores = "";
			for (Error error : responseError.getErrors()) {
				errores = error.getCode() + ": " + error.getMessage() + "; ";
			}
			throw new ApproveErrorException(errores);
		}
		else {
			approveRefund = gson.fromJson(response.getBody(), ApproveResponse.class);
		}

		return approveRefund;

	}

	public CancelResponse cancelRefund(RequestDTO cancelDTO,CancelRequest cancelRequest) throws Exception {

		String url = "";
		
		url = cancelDTO.getUrlHost() + "/paymentservice/globalmerchants/" + cancelDTO.getGlobalMerchants() + "/pos/" + cancelDTO.getPos() + "/payments/" + cancelDTO.getPayments() + "/cancel";
		
		log.debug("cancelRefund() - La url a la que se realiza la llamada " + url);
		String desCert = variablesService.consultarDefinicion(CardosoVariables.CRT_DES_WORLDLINE);
		File cert = resourceLoader.getResource(desCert).getFile();
		String certPassword = variablesService.consultarDefinicion(CardosoVariables.CRT_PASS_WORLDLINE);

		CancelResponse cancelObject = null;

		HttpResponse<String> response = null;

		// Configuración del certificado
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		FileInputStream inputStream = new FileInputStream(cert);
		keyStore.load(inputStream, certPassword.toCharArray());

		SSLContext sslContext = SSLContextBuilder.create().loadKeyMaterial(keyStore, certPassword.toCharArray()).build();

		// Configurar Unirest para usar el cliente HTTP personalizado
		Unirest.setHttpClient(HttpClients.custom().setSSLContext(sslContext).build());

		Gson gson = new Gson();
		String jsonBody = gson.toJson(cancelRequest);

		log.debug("cancelRefund() - El body de la llamada de refund: " + jsonBody);
		response = Unirest.post(url).header("Content-Type", "application/json").body(jsonBody).asString();

		log.debug("cancelRefund() - La respuesta de la llamada es la siguiente: " + response.getStatus() + " - " +  response.getBody());
		int status = response.getStatus();

		if (status > 299) {
			CancelErrorResponse responseError = gson.fromJson(response.getBody(), CancelErrorResponse.class);
			String errores = "";
			for (com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas.cancel.response.error.Error error : responseError.getErrors()) {
				errores = error.getCode() + ": " + error.getMessage() + "; ";
			}
			throw new CancelErrorException(errores);
		}
		else {
			cancelObject = gson.fromJson(response.getBody(), CancelResponse.class);
		}

		return cancelObject;
	}

	public int insertDocument(AuditoriaDTO auditoriaDTO, String codTipoDocumento) throws Exception {
		log.info("insertDocument() - Insertando documento AUDITORIAXML...");
		TipoDocumentoBean documentoAuditoria = null;
		int result = 0;
		try {
			/* ################### TIPO DOCUMENTO ################### */
			comprobarTipoDocumento(codTipoDocumento);

			ContadorBean contador = null;
			documentoAuditoria = sesion.getAplicacion().getDocumentos().getDocumento(codTipoDocumento);
			contador = contadoresService.obtenerContador(documentoAuditoria.getIdContador(), sesion.getAplicacion().getUidActividad());
			if (contador == null) {
				String msgError = "No se ha encontrado el contador para el documento de tipo  [" + documentoAuditoria.getCodtipodocumento() + "].";
				log.info("insertDocument() - " + msgError);
				throw new Exception(msgError);
			}

			/* ################### DOCUMENTO XML ################### */
			AuditoriaXML auditoriaXML = new AuditoriaXML();
			auditoriaXML.setCodalm(sesion.getAplicacion().getCodAlmacen());
			auditoriaXML.setCodcaja(sesion.getAplicacion().getCodCaja());
			auditoriaXML.setComercio(auditoriaDTO.getComercio());
			auditoriaXML.setDesalm(sesion.getAplicacion().getTienda().getDesAlmacen());
			auditoriaXML.setDesusuario(sesion.getSesionUsuario().getUsuario().getDesusuario());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			String formattedDate = sdf.format(new Date());
			auditoriaXML.setFecha(formattedDate);
			auditoriaXML.setIdTipoDocumento(documentoAuditoria.getIdTipoDocumento().intValue());
			auditoriaXML.setImporte(auditoriaDTO.getImporte());
			auditoriaXML.setObservaciones(auditoriaDTO.getObservaciones());
			auditoriaXML.setPaymentId(auditoriaDTO.getPaymentid());
			auditoriaXML.setPaymentIdOrigen(codTipoDocumento.equals(CardosoVariables.CODTIPODOCUMENTO_AUDEV) ? auditoriaDTO.getPaymentIdOrigen() : null);
			auditoriaXML.setPosId(auditoriaDTO.getPosId());
			auditoriaXML.setTipoOperacion(auditoriaDTO.getTipoOperacion());
			auditoriaXML.setUidTicket(UUID.randomUUID().toString());
			auditoriaXML.setUsuario(sesion.getSesionUsuario().getUsuario().getIdUsuario().intValue());

			/* ################### TICKET ################### */
			TicketBean ticket = new TicketBean();
			ticket.setUidActividad(sesion.getAplicacion().getUidActividad());
			ticket.setCodAlmacen(sesion.getAplicacion().getCodAlmacen());
			ticket.setUidTicket(auditoriaXML.getUidTicket());
			ticket.setIdTipoDocumento(documentoAuditoria.getIdTipoDocumento());
			ticket.setCodcaja(sesion.getAplicacion().getCodCaja());
			ticket.setFecha(new Date());
			ticket.setIdTicket(contador.getValor());
			ticket.setCodTicket(contador.getDivisor3());
			ticket.setSerieTicket(contador.getDivisor3());
			ticket.setFirma("*");
			ticket.setLocatorId(ticket.getUidTicket());

			byte[] xmlDocumentoTaxfree = MarshallUtil.crearXML(auditoriaXML);
			ticket.setTicket(xmlDocumentoTaxfree);
			result = ticketMapper.insert(ticket);

			if (result <= 0) {
				throw new Exception();
			}
		}
		catch (Exception e) {
			String msgError = I18N.getTexto("No ha sido posible insertar en base de datos el documento XML de tipo " + documentoAuditoria.getCodtipodocumento());
			log.error("insertDocument() - " + msgError + " : " + e.getMessage(), e);
			throw new Exception(msgError, e);
		}
		return result;
	}

	public void comprobarTipoDocumento(String codTipoDocumento) throws Exception {
		TipoDocumentoBean docTaxFree = null;
		docTaxFree = sesion.getAplicacion().getDocumentos().getDocumento(codTipoDocumento);
		if (docTaxFree == null) {
			String msgError = I18N.getTexto("No se ha encontrado el tipo de documento taxfree [" + codTipoDocumento + "]");
			throw new Exception(msgError);
		}
	}

}
