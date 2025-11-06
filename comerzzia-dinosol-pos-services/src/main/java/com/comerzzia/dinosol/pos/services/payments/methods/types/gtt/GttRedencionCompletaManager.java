package com.comerzzia.dinosol.pos.services.payments.methods.types.gtt;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.services.payments.PaymentDto;
import com.comerzzia.pos.services.payments.PaymentException;
import com.comerzzia.pos.services.payments.events.PaymentOkEvent;

@Component
@Scope("prototype")
public class GttRedencionCompletaManager extends GttManager {

	@Override
	public boolean cancelPay(PaymentDto paymentDto) throws PaymentException {
		/* El pago se elimina directamente porque los pagos de redención completa no se pueden devolver. */
		PaymentOkEvent event = new PaymentOkEvent(this, paymentDto.getPaymentId(), paymentDto.getAmount());
		event.setCanceled(true);
		getEventHandler().paymentOk(event);
		return true;
	}
	
}
