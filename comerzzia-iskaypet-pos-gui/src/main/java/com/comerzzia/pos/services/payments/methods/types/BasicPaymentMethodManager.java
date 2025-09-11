package com.comerzzia.pos.services.payments.methods.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.comerzzia.pos.persistence.mediosPagos.MedioPagoBean;
import com.comerzzia.pos.services.mediospagos.MediosPagosService;
import com.comerzzia.pos.services.payments.configuration.ConfigurationPropertyDto;
import com.comerzzia.pos.services.payments.configuration.PaymentMethodConfiguration;
import com.comerzzia.pos.services.payments.events.PaymentErrorEvent;
import com.comerzzia.pos.services.payments.events.PaymentInitEvent;
import com.comerzzia.pos.services.payments.events.PaymentOkEvent;
import com.comerzzia.pos.services.payments.events.PaymentSelectEvent;
import com.comerzzia.pos.services.payments.events.PaymentStatusEvent;
import com.comerzzia.pos.services.payments.events.listeners.PaymentErrorListener;
import com.comerzzia.pos.services.payments.events.listeners.PaymentOkListener;
import com.comerzzia.pos.services.payments.events.listeners.PaymentProcessListener;
import com.comerzzia.pos.services.payments.events.listeners.PaymentSelectListener;
import com.comerzzia.pos.services.payments.methods.PaymentMethodManager;
import com.comerzzia.pos.services.ticket.ITicket;

@SuppressWarnings("rawtypes")
public abstract class BasicPaymentMethodManager implements PaymentMethodManager {
	
	private Logger log = Logger.getLogger(BasicPaymentMethodManager.class);
	
	public static final String PARAM_RESPONSE_TEF = "PARAM_RESPONSE_TEF";

	protected int paymentId;

	protected String paymentCode;

	protected PaymentEventHandler eventHandler;

	protected List<PaymentProcessListener> listenersProcess;

	protected List<PaymentOkListener> listenersOk;

	protected List<PaymentErrorListener> listenersError;

	protected List<PaymentSelectListener> listenersSelect;

	protected Map<String, Object> parameters;

	protected ITicket ticket;

	protected ITicket ticketOrigen;
	
	protected PaymentMethodConfiguration configuration;
	
	@Autowired
	private MediosPagosService mediosPagosService;

	public BasicPaymentMethodManager() {
		this.listenersProcess = new ArrayList<PaymentProcessListener>();
		this.listenersOk = new ArrayList<PaymentOkListener>();
		this.listenersError = new ArrayList<PaymentErrorListener>();
		this.listenersSelect = new ArrayList<PaymentSelectListener>();
		this.eventHandler = new PaymentEventHandler();
	}

	@Override
	public void initialize() {
		this.paymentId = 0;
		this.parameters = new HashMap<String, Object>();
	}

	public String getPaymentCode() {
		return paymentCode;
	}

	public void setPaymentCode(String paymentCode) {
		this.paymentCode = paymentCode;
	}

	protected PaymentEventHandler getEventHandler() {
		return eventHandler;
	}

	public void setEventHandler(PaymentEventHandler eventHandler) {
		this.eventHandler = eventHandler;
	}
	
	@Override
	public boolean isAvailableForReturn() {
        try {
        	MedioPagoBean medioPago = mediosPagosService.consultarMedioPago(paymentCode);
        	return medioPago.getVisibleDevolucion();
        }
        catch (Exception e) {
        	log.error("canBeUsedToReturn() - Ha habido un error al consultar el medio de pago: " + e.getMessage(), e);
        }
	    return true;
	}

	@Override
	public boolean isAvailable() {
		return true;
	}

	@Override
	public boolean isUniquePayment() {
		return false;
	}

	protected class PaymentEventHandler implements PaymentProcessListener, PaymentErrorListener, PaymentOkListener, PaymentSelectListener {

		@Override
		public void paymentOk(PaymentOkEvent event) {
			firePaymentOkEvent(event);
		}

		@Override
		public void paymentError(PaymentErrorEvent event) {
			firePaymentErrorEvent(event);
		}

		@Override
		public void paymentInitProcess(PaymentInitEvent event) {
			firePaymentInitProcessEvent(event);
		}

		@Override
		public void paymentStatus(PaymentStatusEvent event) {
			firePaymentStatusEvent(event);
		}

		@Override
		public void paymentSelect(PaymentSelectEvent event) {
			firePaymentSelectEvent(event);
		}
	}

	protected void firePaymentOkEvent(PaymentOkEvent event) {
		List<PaymentOkListener> listeners = this.listenersOk;
		for (PaymentOkListener listener : listeners) {
			listener.paymentOk(event);
		}
	}

	public void firePaymentSelectEvent(PaymentSelectEvent event) {
		List<PaymentSelectListener> listeners = this.listenersSelect;
		for (PaymentSelectListener listener : listeners) {
			listener.paymentSelect(event);
		}
	}

	protected void firePaymentErrorEvent(PaymentErrorEvent event) {
		List<PaymentErrorListener> listeners = this.listenersError;
		for (PaymentErrorListener listener : listeners) {
			listener.paymentError(event);
		}
	}

	protected void firePaymentStatusEvent(PaymentStatusEvent event) {
		List<PaymentProcessListener> listeners = this.listenersProcess;
		for (PaymentProcessListener listener : listeners) {
			listener.paymentStatus(event);
		}
	}

	protected void firePaymentInitProcessEvent(PaymentInitEvent event) {
		List<PaymentProcessListener> listeners = this.listenersProcess;
		for (PaymentProcessListener listener : listeners) {
			listener.paymentInitProcess(event);
		}
	}

	@Override
	public void addListenerError(PaymentErrorListener listener) {
		listenersError.add(listener);
	}

	@Override
	public void addListenerOk(PaymentOkListener listener) {
		listenersOk.add(listener);
	}

	@Override
	public void addListenerProcess(PaymentProcessListener listener) {
		listenersProcess.add(listener);
	}
	
	@Override
	public void addListenerSelect(PaymentSelectListener listener) {
		listenersSelect.add(listener);
	}

	@Override
	public void addParameter(String key, Object value) {
		parameters.put(key, value);
	}
	
	@Override
	public void setTicketData(ITicket ticket, ITicket ticketOrigen) {
		this.ticket = ticket;
		this.ticketOrigen = ticketOrigen;
	}
	
	@Override
	public boolean select() {
		this.parameters = new HashMap<String, Object>();
		
		PaymentSelectEvent event = new PaymentSelectEvent(this);
		getEventHandler().paymentSelect(event);
		
		return false;
	}
	
	@Override
	public List<ConfigurationPropertyDto> getConfigurationProperties() {
		return new ArrayList<ConfigurationPropertyDto>();
	}
	
	@Override
	public void setConfiguration(PaymentMethodConfiguration configuration) {
		this.configuration = configuration;
	}
	
	@Override
	public void setPaymentId(int paymentId) {
		this.paymentId = paymentId;
	}
	
	@Override
	public boolean recordCashFlowImmediately() {
	    return true;
	}
	
	@Override
	public boolean isAvailableForExchange() {
		try {
        	MedioPagoBean medioPago = mediosPagosService.getMedioPago(paymentCode);
        	return medioPago.getContado() && medioPago.getEfectivo() && medioPago.getManual();
        }
        catch (Exception e) {
        	log.error("isAvailableForExchange() - Ha habido un error al consultar el medio de pago: " + e.getMessage(), e);
        }
	    return true;
	}

}
