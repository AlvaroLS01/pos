package com.comerzzia.cardoso.pos.services.pagos.worldline.exceptions;

import com.comerzzia.pos.services.payments.PaymentException;

public class WorldlinePaymentException extends PaymentException {

	private static final long serialVersionUID = -3457030879852386540L;

	public WorldlinePaymentException(String message) {
		super(message);
	}

	public WorldlinePaymentException(String message, Throwable exception) {
		super(message, exception);
	}

	public WorldlinePaymentException(String message, Throwable exception, int paymentId, Object source) {
		super(message, exception, paymentId, source);
	}

	public WorldlinePaymentException(String message, Throwable exception, int paymentId, Object source, String errorCode) {
		super(message, exception, paymentId, source);
	}

}
