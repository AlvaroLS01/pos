package com.comerzzia.iskaypet.pos.services.core.sesion.exception;

public class NewPasswordEmptyException extends Exception {

	private static final long serialVersionUID = 9166520685353980970L;

	public NewPasswordEmptyException() {
		super();
	}

	public NewPasswordEmptyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NewPasswordEmptyException(String message, Throwable cause) {
		super(message, cause);
	}

	public NewPasswordEmptyException(String message) {
		super(message);
	}

	public NewPasswordEmptyException(Throwable cause) {
		super(cause);
	}

}
