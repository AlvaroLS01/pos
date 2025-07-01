package com.comerzzia.pos.gui.sales.retail.payments;

import java.util.Comparator;

import com.comerzzia.omnichannel.facade.model.basket.payments.BasketPayment;

public class ComparatorPagoTicket implements Comparator<BasketPayment> {

	@Override
	public int compare(BasketPayment o1, BasketPayment o2) {
		if(o1.getPaymentId() == null) {
			return -1;
		}
		else if(o2.getPaymentId() == null) {
			return 1;
		}
		else {
			return o1.getPaymentId().compareTo(o2.getPaymentId());
		}
	}

}
