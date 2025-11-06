package com.comerzzia.dinosol.pos.services.payments.methods.types.bp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comarch.clm.partner.CLMServiceLocator;
import com.comarch.clm.partner.dto.ExtendedBalanceInquiryRequest;
import com.comarch.clm.partner.dto.ExtendedBalanceInquiryResponse;
import com.comarch.clm.partner.dto.IssuanceRequest;
import com.comarch.clm.partner.dto.IssuanceResponse;
import com.comarch.clm.partner.dto.IssuanceReversalRequest;
import com.comarch.clm.partner.dto.IssuanceReversalResponse;
import com.comarch.clm.partner.dto.VirtualMoneyBurnt;
import com.comarch.clm.partner.exception.BpConfiguracionException;
import com.comarch.clm.partner.exception.BpRespuestaException;
import com.comarch.clm.partner.exception.BpSoapException;
import com.comarch.clm.partner.util.CLMServiceErrorString;
import com.comarch.clm.partner.util.CLMServiceFomatString;
import com.comerzzia.dinosol.pos.util.xml.ObjectParseUtil;
import com.comerzzia.pos.services.payments.PaymentDto;
import com.comerzzia.pos.services.payments.PaymentException;
import com.comerzzia.pos.services.payments.configuration.ConfigurationPropertyDto;
import com.comerzzia.pos.services.payments.configuration.PaymentMethodConfiguration;
import com.comerzzia.pos.services.payments.events.PaymentInitEvent;
import com.comerzzia.pos.services.payments.events.PaymentOkEvent;
import com.comerzzia.pos.services.payments.methods.types.BasicPaymentMethodManager;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;

@Component
@Scope("prototype")
public class BPManager extends BasicPaymentMethodManager {

	protected static final Logger log = Logger.getLogger(BPManager.class);

	public static final String PARAM_TARJETA_BP = "PARAM_TARJETA_BP";
	public static final String PARAM_NUMERO_TARJETA_BP = "PARAM_NUMERO_TARJETA_BP";
	public static final String VIRTUAL_MONEY_CODE = "TRG_€HD";

	protected CLMServiceLocator service;

	protected DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

	protected String url;

	protected BigDecimal tramo;
	protected BigDecimal eurosTramo;
	protected boolean condicionQuemado;;

	public CLMServiceLocator getService() {
		return service;
	}

	public BPManager() {
		service = new CLMServiceLocator();
	}

	@Override
	public void setConfiguration(PaymentMethodConfiguration configuration) {
		url = configuration.getConfigurationProperty("url");
		service.setCLMServiceSOAPEndpointAddress(url);
		tramo = StringUtils.isNotBlank(configuration.getConfigurationProperty("tramo")) ? new BigDecimal(configuration.getConfigurationProperty("tramo")) : BigDecimal.ZERO;
		eurosTramo = StringUtils.isNotBlank(configuration.getConfigurationProperty("euros_tramo")) ? new BigDecimal(configuration.getConfigurationProperty("euros_tramo")) : BigDecimal.ZERO;
		
		String condiciones = configuration.getConfigurationProperty("condiciones");
		condicionQuemado = StringUtils.isNotBlank(condiciones) && condiciones.equals("-") ? false : true;
	}

	@Override
	public boolean pay(BigDecimal amount) throws PaymentException {
		if(tramo == null || eurosTramo == null || BigDecimalUtil.isMenorACero(tramo) || BigDecimalUtil.isMenorACero(eurosTramo)) {
			throw new PaymentException(I18N.getTexto("No está configurado el pago con la tarjeta BP."));
		}
		
		BigDecimal total = ticket.getCabecera().getTotales().getTotalAPagar();
		BigDecimal saldoDisponible = BigDecimal.ZERO;
		if(BigDecimalUtil.isMayorACero(tramo)) {
			if(isCondicionQuemadoPositiva() && BigDecimalUtil.isMayorACero(total)) {
				saldoDisponible = total.divide(tramo, 0, RoundingMode.FLOOR);
			}
			else {
				saldoDisponible = total.divide(tramo, 0, RoundingMode.CEILING);
			}
		}
		saldoDisponible = BigDecimalUtil.redondear(saldoDisponible.multiply(eurosTramo));
		
		if(BigDecimalUtil.isMayor(amount, saldoDisponible)) {
			String mensaje = I18N.getTexto("El importe supera el máximo permitido por el programa DinoBP respecto al total de la compra.")
					+ System.lineSeparator()
					+ I18N.getTexto("El importe máximo permitido es {0} € por cada {1} € de compra.", FormatUtil.getInstance().formateaImporte(eurosTramo), FormatUtil.getInstance().formateaImporte(tramo));
			
			throw new PaymentException(mensaje);
		}
		
		boolean importeTieneDecimales = !BigDecimalUtil.isIgualACero(amount.remainder(BigDecimal.ONE));
		if(importeTieneDecimales) {
			throw new PaymentException(I18N.getTexto("El importe a pagar con la tarjeta BP no puede tener decimales."));
		}
		
		getEventHandler().paymentInitProcess(new PaymentInitEvent(this));

		String numeroTarjeta = (String) parameters.get(PARAM_NUMERO_TARJETA_BP);

		IssuanceResponse response = null;
		try {
			response = realizarMovimiento(amount, numeroTarjeta, null);
		}
		catch (Exception e) {
			throw new PaymentException(I18N.getTexto("Se ha producido un error al conectar con el servicio CLM"), e, paymentId, this);
		}

		/* Transaccion correcta */
		if ("0".equals(response.getErrorCode())) {
			Map<String, Object> extendedData = ObjectParseUtil.introspect(response);

			/* Agregar mensaje parseado con los codigos de mensajes y parametros recibidos */
			extendedData.put("formatedText", CLMServiceFomatString.getFormatedString(response.getText()));
			extendedData.put("numeroTarjeta", numeroTarjeta);
			extendedData.put("transactionDate", response.getTransactionDate());
			extendedData.put("receipID", response.getReceiptId());
			extendedData.put("locationCode", response.getLocationCode());

			PaymentOkEvent event = new PaymentOkEvent(this, paymentId, amount);
			event.addExtendedData(PARAM_RESPONSE_TEF, response);
			event.setExtendedData(extendedData);
			getEventHandler().paymentOk(event);

			return true;
		}
		else {
			throw new PaymentException(I18N.getTexto(CLMServiceErrorString.getErrorString(response.getErrorCode())), null, paymentId, this, response.getErrorCode());
		}
	}

	public IssuanceResponse realizarMovimiento(BigDecimal amount, String numeroTarjeta, BigDecimal puntosBp) throws BpSoapException {
		IssuanceResponse response = null;
		IssuanceRequest request = new IssuanceRequest();
		request.setType("I");
		request.setTransactionDate(dateFormat.format(new Date()));
		request.setCardNo(numeroTarjeta);
		request.setPartnerId("HD");
		request.setLocationCode("SU" + ticket.getCabecera().getTienda().getCodAlmacen());		
		request.setAmount(0);
		request.setVirtualMoneyPartner(0);
		request.setOnline(true);
				
		if (amount != null) {
			// quemando dinero virtual
			
			request.setVirtualMoneyBurntList(new VirtualMoneyBurnt[] { new VirtualMoneyBurnt(VIRTUAL_MONEY_CODE, amount.doubleValue()) });
			request.setAmountToCalculation(0);
			
			// se utiliza un valor aleatorio porque el API de BP no permite realizar dos transacciones (quemado y suma de puntos) en
			// transacciones independientes.
			request.setReceiptId(String.valueOf(new Random().nextInt(99999) + 100000));
			
			log.debug("realizarMovimiento() - Petición quemado: " + request.getTransactionDate() + "/" + request.getPartnerId() + "/" + request.getLocationCode() + "/" + request.getReceiptId());
		} else {
			// acumulando dinero virtual			
			
			request.setVirtualMoneyBasicRules(puntosBp.doubleValue());
			request.setAmountToCalculation(ticket.getTotales().getTotalAPagar().doubleValue());
			
			request.setAmount(ticket.getTotales().getTotalAPagar().doubleValue());
			
			// se utiliza en ReceiptId el formato CCCTTTT (codigo de caja-ultimos 4 digitos del ticket)
			// por compatibilidad con los procesos nocturnos del cuadre con los movimientos de la SA
			request.setReceiptId(StringUtils.leftPad(ticket.getCabecera().getCodCaja(), 3, '0') + 
					             StringUtils.right(ticket.getCabecera().getCodTicket(), 4));
			
			log.debug("realizarMovimiento() - Petición acumulacion: " + request.getTransactionDate() + "/" + request.getPartnerId() + "/" + request.getLocationCode() + "/" + request.getReceiptId());
		}

		
		try {
			log.debug("realizarMovimiento() - Realizando petición a BP");	
			log.debug("realizarMovimiento() - URL: " + url);
			log.debug("realizarMovimiento() - Type: " + request.getType());
			log.debug("realizarMovimiento() - setTransactionDate: " + request.getTransactionDate());			
			log.debug("realizarMovimiento() - CardNo: " + request.getCardNo());
			log.debug("realizarMovimiento() - PartnerId: " + request.getPartnerId());
			log.debug("realizarMovimiento() - LocationCode: " + request.getLocationCode());
			log.debug("realizarMovimiento() - Amount: " + request.getAmount());
			log.debug("realizarMovimiento() - VirtualMoneyPartner: " + request.getVirtualMoneyPartner());
			log.debug("realizarMovimiento() - Online: " + request.isOnline());
			log.debug("realizarMovimiento() - VirtualMoneyBurntList: " + request.getVirtualMoneyBurntList());
			log.debug("realizarMovimiento() - AmountToCalculation: " + request.getAmountToCalculation());
			log.debug("realizarMovimiento() - ReceiptId: " + request.getReceiptId());
			log.debug("realizarMovimiento() - VirtualMoneyBasicRules: " + request.getVirtualMoneyBasicRules());
			
			response = service.getCLMServiceSOAP().issuance(request);
			log.debug("realizarMovimiento() - Identificador de la transacción: " + response.getTransactionId());
		}
		catch (Exception e) {
			String mensajeError = "Se ha producido un error al conectar con el servicio CLM";
			log.error("realizarMovimiento() - " + mensajeError + " - " + e.getMessage());
			throw new BpSoapException(I18N.getTexto(mensajeError));
		}
		return response;
	}

//	@Override
//	public boolean isLongTask() {
//		return true;
//	}

	@Override
	public boolean returnAmount(BigDecimal amount) throws PaymentException {
		throw new PaymentException(I18N.getTexto("No se puede devolver en este medio de pago."));
	}

	@Override
	public boolean cancelPay(PaymentDto paymentDto) throws PaymentException {
		IssuanceReversalRequest request = new IssuanceReversalRequest();
		request.setType("U");
		request.setTransactionDate(dateFormat.format(new Date()));
		request.setPartnerId("HD");
		request.setLocationCode("SU" + ticket.getCabecera().getTienda().getCodAlmacen());
		int x = new Random().nextInt(99999) + 100000;
		request.setReceiptId(String.valueOf(x));
		request.setCardNo((String) paymentDto.getExtendedData().get("numeroTarjeta"));
		/* Datos del pago que se desea anular */
		request.setOriginalDate((String) paymentDto.getExtendedData().get("transactionDate"));
		request.setOriginalReceiptId(Long.parseLong((String) paymentDto.getExtendedData().get("receipID")));
		request.setOriginalLocationCode((String) paymentDto.getExtendedData().get("locationCode"));

		request.setAmountToCalculation(0);
		request.setAmount(0);
		request.setOnline(false);
		log.debug("cancelPay() - Identificador de transacción : " + request.getTransactionDate() + request.getPartnerId() + request.getLocationCode() + request.getReceiptId());

		IssuanceReversalResponse response = null;
		try {
			response = service.getCLMServiceSOAP().issuanceReversal(request);
		}
		catch (Exception e) {
			throw new PaymentException(I18N.getTexto("Se ha producido un error al conectar con el servicio CLM"), e, paymentId, this);
		}

		/* Transaccion correcta */
		if ("0".equals(response.getErrorCode())) {
			Map<String, Object> extendedData = ObjectParseUtil.introspect(response);
			/* Agregar mensaje parseado con los codigos de mensajes y parametros recibidos */
			extendedData.put("formatedText", CLMServiceFomatString.getFormatedString(response.getText()));

			PaymentOkEvent event = new PaymentOkEvent(this, paymentDto.getPaymentId(), paymentDto.getAmount());
			event.setCanceled(true);
			event.addExtendedData(PARAM_RESPONSE_TEF, response);
			event.setExtendedData(extendedData);
			getEventHandler().paymentOk(event);
			return true;
		}
		else {
			/* Error controlado */
			throw new PaymentException(I18N.getTexto(CLMServiceErrorString.getErrorString(response.getErrorCode())), null, paymentId, this, response.getErrorCode());
		}
	}

	@Override
	public boolean cancelReturn(PaymentDto paymentDto) {
		return false;
	}

	@Override
	public List<ConfigurationPropertyDto> getConfigurationProperties() {
		List<ConfigurationPropertyDto> configurationProperties = new ArrayList<ConfigurationPropertyDto>();
		return configurationProperties;
	}

	/**
	 * Realiza una petición para traer el saldo de una tarjeta.
	 * 
	 * @param numeroTarjeta
	 *            : Número de la tarjeta a consultar.
	 * @return response
	 * @throws BpSoapException
	 * @throws BpRespuestaException
	 * @throws BpConfiguracionException
	 */
	public ExtendedBalanceInquiryResponse getSaldo(String numeroTarjeta) throws BpSoapException, BpRespuestaException, BpConfiguracionException {
		/* Realizamos la comprobación de la configuración de BP (URL) */
		try {
			comprobarConfiguracion();
		}
		catch (BpConfiguracionException e) {
			log.error("getSaldo() - " + e.getMessage());
			throw new BpConfiguracionException(I18N.getTexto(e.getMessage()));
		}

		ExtendedBalanceInquiryRequest request = new ExtendedBalanceInquiryRequest();
		request.setType("B");
		request.setCardNo(numeroTarjeta);
		request.setPartnerId("HD");
		request.setTransactionDate(dateFormat.format(new Date()));
		request.setLocationCode("SU" + ticket.getCabecera().getTienda().getCodAlmacen());
		request.setReceiptId("0");

		ExtendedBalanceInquiryResponse response = null;
		try {
			log.debug("getSaldo() - Realizando petición a BP");
			log.debug("getSaldo() - URL: " + url);
			log.debug("getSaldo() - Type: " + request.getType());
			log.debug("getSaldo() - CardNo: " + request.getCardNo());
			log.debug("getSaldo() - PartnerId: " + request.getPartnerId());
			log.debug("getSaldo() - setTransactionDate: " + request.getTransactionDate());			
			log.debug("getSaldo() - LocationCode: " + request.getLocationCode());
			log.debug("getSaldo() - ReceiptId: " + request.getReceiptId());
			
			response = service.getCLMServiceSOAP().extendedBalanceInquiry(request);
		}
		catch (Exception e1) {
			String mensajeError = "Se ha producido un error al conectar con el servicio CLM";
			log.error("getSaldo() - " + mensajeError + " - " + e1.getMessage());
			throw new BpSoapException(I18N.getTexto(mensajeError));
		}

		/*
		 * Comprobamos que la transacción se ha realizado correctamente, en caso de no ser asi se enviará un exception
		 * de tipo respuesta
		 */
		if (!"0".equals(response.getErrorCode()) && !"105".equals(response.getErrorCode())) {
			String mensajeError = response.getErrorCode() + "/" + CLMServiceErrorString.getErrorString(response.getErrorCode());
			log.error("getSaldo() - " + mensajeError);
			throw new BpRespuestaException(mensajeError);
		}
		return response;
	}

	/**
	 * Realiza la comprobación de que la configuración de la pasarela está rellena.
	 * 
	 * @throws BpConfiguracionException
	 */
	public void comprobarConfiguracion() throws BpConfiguracionException {
		if (StringUtils.isBlank(url)) {
			String mensajeError = "La pasarela de BP no está configurada correctamente";
			log.error("comprobarConfiguracion() - " + mensajeError);
			throw new BpConfiguracionException(mensajeError);
		}
	}

	public BigDecimal getTramo() {
		return tramo;
	}

	public BigDecimal getEurosTramo() {
		return eurosTramo;
	}

	public boolean isCondicionQuemadoPositiva() {
		return condicionQuemado;
	}
	
	@Override
	public boolean isUniquePayment() {
		return true;
	}
	
    public String getUrl() {
    	return url;
    }

}
