package com.comerzzia.dinosol.pos.services.payments.methods.types.vales;

import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.services.payments.PaymentDto;
import com.comerzzia.pos.services.payments.PaymentException;
import com.comerzzia.pos.services.payments.events.PaymentInitEvent;
import com.comerzzia.pos.services.payments.events.PaymentOkEvent;
import com.comerzzia.pos.services.payments.methods.types.BasicPaymentMethodManager;

@Component
@Scope("prototype")
public class ValesManager extends BasicPaymentMethodManager {

	public static final String PARAM_NUMERO_VALE = "numeroVale";
	protected static final Logger log = Logger.getLogger(ValesManager.class);
	
	@Override
    public boolean pay(BigDecimal amount) throws PaymentException {
		getEventHandler().paymentInitProcess(new PaymentInitEvent(this));
		
		String numeroVale = (String) parameters.get(PARAM_NUMERO_VALE);
		log.debug("pay() - Se ha producido un pago para el vale : " + numeroVale);
		
		PaymentOkEvent event = new PaymentOkEvent(this, paymentId, amount);
		event.addExtendedData(PARAM_NUMERO_VALE, numeroVale);
		getEventHandler().paymentOk(event);
		return true;
    }

	@Override
    public boolean returnAmount(BigDecimal amount) throws PaymentException {
	    return false;
    }

	@Override
    public boolean cancelPay(PaymentDto payment)  throws PaymentException {
		String numeroVale = (String) payment.getExtendedData().get(PARAM_NUMERO_VALE);
		log.debug("cancelPay() - Se ha cancelado el pago del vale : " + numeroVale);
		
		PaymentOkEvent event = new PaymentOkEvent(this, payment.getPaymentId(), payment.getAmount());
		event.setCanceled(true);
		event.addExtendedData(PARAM_NUMERO_VALE, numeroVale);
		getEventHandler().paymentOk(event);
		return true;
    }

	@Override
    public boolean cancelReturn(PaymentDto payment) throws PaymentException {
	    return false;
    }

}
