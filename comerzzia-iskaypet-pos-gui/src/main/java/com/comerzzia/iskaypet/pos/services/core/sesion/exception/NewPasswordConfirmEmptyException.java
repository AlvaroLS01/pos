package com.comerzzia.iskaypet.pos.services.core.sesion.exception;

public class NewPasswordConfirmEmptyException extends Exception {

	private static final long serialVersionUID = -4808552017615363972L;

	public NewPasswordConfirmEmptyException() {
		super();
	}

	public NewPasswordConfirmEmptyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NewPasswordConfirmEmptyException(String message, Throwable cause) {
		super(message, cause);
	}

	public NewPasswordConfirmEmptyException(String message) {
		super(message);
	}

	public NewPasswordConfirmEmptyException(Throwable cause) {
		super(cause);
	}

}
