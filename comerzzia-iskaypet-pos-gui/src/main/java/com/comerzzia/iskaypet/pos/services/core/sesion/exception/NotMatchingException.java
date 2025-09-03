package com.comerzzia.iskaypet.pos.services.core.sesion.exception;

public class NotMatchingException extends Exception {

	private static final long serialVersionUID = 2325600325798961845L;

	public NotMatchingException() {
		super();
	}

	public NotMatchingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NotMatchingException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotMatchingException(String message) {
		super(message);
	}

	public NotMatchingException(Throwable cause) {
		super(cause);
	}

}
