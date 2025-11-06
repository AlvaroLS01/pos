package com.comerzzia.dinosol.pos.services.payments.methods.types.tefvirtual;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

import com.comerzzia.pos.services.payments.PaymentDto;
import com.comerzzia.pos.services.payments.PaymentException;
import com.comerzzia.pos.services.payments.events.PaymentInitEvent;
import com.comerzzia.pos.services.payments.events.PaymentOkEvent;
import com.comerzzia.pos.services.payments.methods.types.BasicPaymentMethodManager;
import com.comerzzia.pos.services.ticket.pagos.tarjeta.DatosPeticionPagoTarjeta;
import com.comerzzia.pos.services.ticket.pagos.tarjeta.DatosRespuestaPagoTarjeta;

public abstract class TefBasicManager extends BasicPaymentMethodManager {

	private Logger log = Logger.getLogger(TefBasicManager.class);

	@Override
	public boolean pay(BigDecimal amount) throws PaymentException {
		log.debug("pay() - Making payment request");

		PaymentInitEvent initEvent = new PaymentInitEvent(this);
		getEventHandler().paymentInitProcess(initEvent);

		try {
			conect();

			DatosRespuestaPagoTarjeta response = createResponse(amount);

			doPaymentRequest(amount, response);

			throwPaymentOkEvent(amount, response);

			return true;
		}
		catch (Throwable e) {
			log.error("pay() - Error while making payment request:" + e.getMessage(), e);

			cancel();

			throw new PaymentException(e.getMessage(), e, paymentId, this);
		}
		finally {
			try {
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

		PaymentInitEvent initEvent = new PaymentInitEvent(this);
		getEventHandler().paymentInitProcess(initEvent);

		try {
			conect();

			DatosRespuestaPagoTarjeta response = createResponse(amount);

			doReturnRequest(amount, response);

			throwPaymentOkEvent(amount, response);

			return true;
		}
		catch (Throwable e) {
			log.error("returnAmount() - Error while making payment request:" + e.getMessage(), e);

			cancel();

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

	@Override
	public boolean cancelReturn(PaymentDto payment) throws PaymentException {
		log.debug("cancelReturn() - Making payment cancel request");

		PaymentInitEvent initEvent = new PaymentInitEvent(this);
		getEventHandler().paymentInitProcess(initEvent);

		try {
			conect();

			DatosRespuestaPagoTarjeta response = createCancelResponse(payment);

			doReturnCancelRequest(payment, response);

			throwPaymentCancellationOkEvent(payment, response);

			return true;
		}
		catch (Throwable e) {
			log.error("cancelReturn() - Error while making payment cancel request:" + e.getMessage(), e);

			cancel();

			throw new PaymentException(e.getMessage(), e, paymentId, this);
		}
		finally {
			try {
				disconect();
			}
			catch (Exception e) {
				log.error("cancelReturn() - Error while disconect: " + e.getMessage(), e);
			}
		}
	}

	protected void throwPaymentOkEvent(BigDecimal amount, DatosRespuestaPagoTarjeta response) {
		PaymentOkEvent event = new PaymentOkEvent(this, paymentId, amount);
		event.addExtendedData(PARAM_RESPONSE_TEF, response);
		getEventHandler().paymentOk(event);
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

	protected DatosRespuestaPagoTarjeta createCancelResponse(PaymentDto payment) {
		return createResponse(payment.getAmount());
	}

	/**
	 * Make the connection with the payment gateway, either through a service, a DLL, a TCP socket, etc.
	 * @throws Exception 
	 */
	public abstract void conect() throws Exception;

	/**
	 * Makes the disconnection of the payment gateway
	 */
	public abstract void disconect() throws Exception;

	/**
	 * Cancel the operation in progress. It must be implemented in case there is an error after receiving the response from the payment gateway.
	 */
	public abstract void cancel();

	/**
	 * Makes the payment transaction and fills the response.
	 * @param amount Amount to pay.
	 * @param response Response to fill.
	 * @throws Exception 
	 */
	protected abstract void doPaymentRequest(BigDecimal amount, DatosRespuestaPagoTarjeta response) throws Exception;

	/**
	 * Makes the return transaction and fills the response.
	 * @param amount Amount to pay.
	 * @param response Response to fill.
	 * @throws Exception 
	 */
	protected abstract void doReturnRequest(BigDecimal amount, DatosRespuestaPagoTarjeta response) throws Exception;

	/**
	 * Makes the payment cancel transaction and fills the response.
	 * @param PaymentDto Payment to cancel.
	 * @param response Response to fill.
	 * @throws Exception 
	 */
	protected abstract void doPaymentCancelRequest(PaymentDto payment, DatosRespuestaPagoTarjeta response) throws Exception;

	/**
	 * Makes the return cancel transaction and fills the response.
	 * @param PaymentDto Payment to cancel.
	 * @param response Response to fill.
	 * @throws Exception 
	 */
	protected abstract void doReturnCancelRequest(PaymentDto payment, DatosRespuestaPagoTarjeta response) throws Exception;

}
