package com.comerzzia.iskaypet.pos.devices.tarjeta;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

import com.comerzzia.pos.dispositivo.tarjeta.TefBasicManager;
import com.comerzzia.pos.services.payments.PaymentDto;
import com.comerzzia.pos.services.payments.PaymentException;
import com.comerzzia.pos.services.payments.events.PaymentInitEvent;
import com.comerzzia.pos.services.payments.events.PaymentOkEvent;
import com.comerzzia.pos.services.ticket.pagos.tarjeta.DatosPeticionPagoTarjeta;
import com.comerzzia.pos.services.ticket.pagos.tarjeta.DatosRespuestaPagoTarjeta;

public abstract class IskTefBasicManager extends TefBasicManager {

	private Logger log = Logger.getLogger(TefBasicManager.class);

	@Override
	public boolean cancelPay(PaymentDto payment) throws PaymentException {
		log.debug("cancelPay() - Making payment cancel request");

		PaymentInitEvent initEvent = new PaymentInitEvent(this);
		getEventHandler().paymentInitProcess(initEvent);

		try {
			conect();
			paymentId = payment.getPaymentId();
			DatosRespuestaPagoTarjeta response = createCancelResponse(payment);

			doPaymentCancelRequest(payment, response);

			throwPaymentCancellationOkEvent(payment, response);

			return true;
		}
		catch (Throwable e) {
			log.error("cancelPay() - Error while making payment cancel request:" + e.getMessage(), e);

			cancel();

			throw new PaymentException(e.getMessage(), e, paymentId, this);
		}
		finally {
			try {
				disconect();
			}
			catch (Exception e) {
				log.error("cancelPay() - Error while disconect: " + e.getMessage(), e);
			}
		}
	}

	private void throwPaymentCancellationOkEvent(PaymentDto payment, DatosRespuestaPagoTarjeta response) {
		PaymentOkEvent event = new PaymentOkEvent(this, payment.getPaymentId(), payment.getAmount());
		event.addExtendedData(PARAM_RESPONSE_TEF, response);
		event.setCanceled(true);
		getEventHandler().paymentOk(event);
	}

	protected DatosRespuestaPagoTarjeta createResponse(BigDecimal amount) {
		log.debug("createResponse() - Creando el objeto DatosRespuestaPagoTarjeta a partir de los siguientes valores:");
		log.debug("createResponse() - CodTicket: " + ticket.getCabecera().getCodTicket());
		log.debug("createResponse() - IdTicket: " + ticket.getCabecera().getIdTicket());
		log.debug("createResponse() - Amount: " + amount);
		log.debug("createResponse() - Empleado: " + ticket.getCabecera().getCajero().getUsuario());
		
		String codTicket = ticket.getCabecera().getCodTicket();
		Long idTicket = ticket.getCabecera().getIdTicket();
		DatosPeticionPagoTarjeta requestData = new DatosPeticionPagoTarjeta(codTicket, idTicket, amount);
		requestData.setEmpleado(ticket.getCabecera().getCajero().getUsuario());

		DatosRespuestaPagoTarjeta response = new DatosRespuestaPagoTarjeta(requestData);
		return response;
	}

}
