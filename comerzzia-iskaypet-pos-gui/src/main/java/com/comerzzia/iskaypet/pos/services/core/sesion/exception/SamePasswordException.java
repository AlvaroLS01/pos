package com.comerzzia.iskaypet.pos.services.core.sesion.exception;

public class SamePasswordException extends Exception {

	private static final long serialVersionUID = 6412107120384224359L;

	public SamePasswordException() {
		super();
	}

	public SamePasswordException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public SamePasswordException(String message, Throwable cause) {
		super(message, cause);
	}

	public SamePasswordException(String message) {
		super(message);
	}

	public SamePasswordException(Throwable cause) {
		super(cause);
	}

}
