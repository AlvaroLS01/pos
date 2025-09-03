package com.comerzzia.iskaypet.pos.devices.tarjeta.sipay;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.iskaypet.librerias.sipay.client.constants.SipayConstants;
import com.comerzzia.iskaypet.pos.devices.tarjeta.IskTefBasicManager;
import com.comerzzia.iskaypet.pos.services.payments.methods.types.sipay.SipayService;
import com.comerzzia.pos.services.payments.PaymentDto;
import com.comerzzia.pos.services.payments.PaymentException;
import com.comerzzia.pos.services.payments.configuration.ConfigurationPropertyDto;
import com.comerzzia.pos.services.payments.configuration.PaymentMethodConfiguration;
import com.comerzzia.pos.services.payments.events.PaymentInitEvent;
import com.comerzzia.pos.services.ticket.ITicket;
import com.comerzzia.pos.services.ticket.pagos.PagoTicket;
import com.comerzzia.pos.services.ticket.pagos.tarjeta.DatosRespuestaPagoTarjeta;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.i18n.I18N;

@Component
@Scope("prototype")
@SuppressWarnings("unchecked")
public class TefSipayManager extends IskTefBasicManager {
	
	private static final Logger log = Logger.getLogger(TefSipayManager.class);
	@Autowired
	private SipayService sipayService;
	
	@Override
	public List<ConfigurationPropertyDto> getConfigurationProperties() {
		List<ConfigurationPropertyDto> properties = new ArrayList<>();
		properties.add(new ConfigurationPropertyDto(SipayConstants.STORE_ID, I18N.getTexto("Id tienda")));
		properties.add(new ConfigurationPropertyDto(SipayConstants.POS_ID, I18N.getTexto("Id pos")));
		properties.add(new ConfigurationPropertyDto(SipayConstants.LANGUAGE, I18N.getTexto("Idioma")));
		properties.add(new ConfigurationPropertyDto(SipayConstants.URL, I18N.getTexto("URL")));

		return properties;
	}
	
	@Override
	public void setConfiguration(PaymentMethodConfiguration configuration) {
		super.setConfiguration(configuration);
		sipayService.setConfiguration(configuration);
	}

	@Override
	public void conect() throws Exception { 
		sipayService.conect();
	}

	@Override
	public void disconect() {
		log.info("disconect()...");
	}
	
	@Override
	protected void doPaymentRequest(BigDecimal amount, DatosRespuestaPagoTarjeta response) throws TimeoutException, Exception {
		sipayService.doPaymentRequest(amount, response, ticket);
	}
	
	@Override
	protected void doReturnRequest(BigDecimal amount, DatosRespuestaPagoTarjeta response) throws TimeoutException, Exception {
		sipayService.doReturnRequest(amount, response, ticket);
	}

	@Override
	protected void doPaymentCancelRequest(PaymentDto payment, DatosRespuestaPagoTarjeta response) throws Exception {
		log.info("doPaymentCancelRequest()...");
		String sequence = "";
		if (payment.getExtendedData().get("PARAM_RESPONSE_TEF") != null) {
			DatosRespuestaPagoTarjeta datosPagoOrigen = (DatosRespuestaPagoTarjeta) payment.getExtendedData().get("PARAM_RESPONSE_TEF");
			if (StringUtils.isNotBlank(datosPagoOrigen.getNumOperacion())) {
				sequence = "sq:" + datosPagoOrigen.getNumOperacion();
			}
		}
		sipayService.cancelarOperacion(sequence, sipayService.getAmountOperation(paymentId, paymentCode, ticket), ticket);
	}
	
	@Override
	public void cancel() {
		log.info("cancel()...");
		try {
			sipayService.cancelarOperacion("",sipayService.getAmountOperation(paymentId, paymentCode, ticket), ticket);
		} catch (Exception e) {
			log.error("cancel() - Error al cancelar la operación");
		}

	}

	@Override
	protected void doReturnCancelRequest(PaymentDto payment, DatosRespuestaPagoTarjeta response) throws Exception {
		sipayService.doPaymentCancelRequest(payment, response, ticket);
	}

	@Override
	public boolean pay(BigDecimal amount) throws PaymentException {
        try {
            sipayService.isKeepingAlive();
            Thread.sleep(SipayService.PARAMETRO_MILISEGUNDOS_SLEEP_KEEP_ALIVE);
        } catch (Exception e) {
            log.error("pay() - Error while checking connection: " + e.getMessage(), e);
            throw new PaymentException(I18N.getTexto(e.getMessage()), e, paymentId, this);
        }
        // Sipay detecta una duplicidad cuando existen dos pagos en el mismo ticket con el mismo importe, por lo que no lo permitiremos
		for (PagoTicket pago : (List<PagoTicket>) ticket.getPagos()) {
			if (pago.getDatosRespuestaPagoTarjeta() != null && pago.getDatosRespuestaPagoTarjeta().getAdicionales() != null
					&& pago.getDatosRespuestaPagoTarjeta().getAdicional("sipay") != null
					&& BigDecimalUtil.isIgual(amount.setScale(2, RoundingMode.FLOOR), pago.getImporte().setScale(2, RoundingMode.FLOOR))) {
				throw new PaymentException("No pueden registrarse dos pagos EFT con el mismo importe");
			}
		}
		
		PaymentInitEvent initEvent = new PaymentInitEvent(this);
		getEventHandler().paymentInitProcess(initEvent);

		try {
			//Antes de conectar con sipay guardamos la información del pago en el caso de reinicio
			sipayService.keepTransactionSipay(amount, ticket, paymentId);
			conect();
			DatosRespuestaPagoTarjeta response = createResponse(amount);
			sipayService.completarResponse(response, ticket);
			response.setTipoTransaccion("P");
			doPaymentRequest(amount, response);
			throwPaymentOkEvent(amount, response);
			
			return true;
		
		} catch (TimeoutException toe) {
			log.error("pay() - Error while making payment request:" + toe.getMessage(), toe);
			cancel();
			throw new PaymentException(toe.getMessage(), toe, paymentId, this);
			
		} catch (Throwable e) {
			log.error("pay() - Error while making payment request:" + e.getMessage(), e);
			throw new PaymentException(e.getMessage(), e, paymentId, this);
			
		}
		finally {
			try {
				//Si no hubo reinicio limpiamos del ticket la transacción Sipay
				sipayService.clearTransactionSipay(ticket);
				disconect();
			}
			catch (Exception e) {
				log.error("pay() - Error while disconect: " + e.getMessage(), e);
			}
		}
	}

	@Override
	public boolean returnAmount(BigDecimal amount) throws PaymentException {
		log.debug("returnAmount() - Making return request");
        try {
            sipayService.isKeepingAlive();
            Thread.sleep(SipayService.PARAMETRO_MILISEGUNDOS_SLEEP_KEEP_ALIVE);
        } catch (Exception e) {
            log.error("pay() - Error while checking connection: " + e.getMessage(), e);
            throw new PaymentException(I18N.getTexto(e.getMessage()), e, paymentId, this);
        }
		PaymentInitEvent initEvent = new PaymentInitEvent(this);
		getEventHandler().paymentInitProcess(initEvent);

		try {
			conect();
			DatosRespuestaPagoTarjeta response = createResponse(amount);
			sipayService.completarResponse(response, ticket);
			response.setTipoTransaccion("R");
			doReturnRequest(amount, response);
			throwPaymentOkEvent(amount, response);

			return true;
			
		} catch (TimeoutException toe) {
			log.error("returnAmount() - Error while making return request:" + toe.getMessage(), toe);
			cancel();
			throw new PaymentException(toe.getMessage(), toe, paymentId, this);
			
		} catch (Throwable e) {
			log.error("returnAmount() - Error while making return request:" + e.getMessage(), e);
			throw new PaymentException(e.getMessage(), e, paymentId, this);
		}
		finally {
			try {
				disconect();
			}
			catch (Exception e) {
				log.error("returnAmount() - Error while disconect: " + e.getMessage(), e);
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	public void cancelPayPendingTicket(ITicket ticket) throws Exception {
		sipayService.cancelPayPendingTicket(paymentId, paymentCode, ticket);
	}

}
