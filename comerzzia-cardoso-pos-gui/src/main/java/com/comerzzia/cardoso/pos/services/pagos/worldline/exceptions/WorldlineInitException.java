package com.comerzzia.cardoso.pos.services.pagos.worldline.exceptions;

import com.comerzzia.pos.services.payments.PaymentException;

public class WorldlineInitException extends PaymentException {

	private static final long serialVersionUID = -7734584080512880758L;

	public WorldlineInitException(String message) {
		super(message);
	}

	public WorldlineInitException(String message, Throwable exception) {
		super(message, exception);
	}

	public WorldlineInitException(String message, Throwable exception, int paymentId, Object source) {
		super(message, exception, paymentId, source);
	}

	public WorldlineInitException(String message, Throwable exception, int paymentId, Object source, String errorCode) {
		super(message, exception, paymentId, source);
	}

}
