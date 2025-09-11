package com.comerzzia.pos.services.core.listeners;

public class POSListenerException extends com.comerzzia.pos.util.exception.Exception {

	private static final long serialVersionUID = 1L;

	public POSListenerException() {
		super();
	}

	public POSListenerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public POSListenerException(String message, Throwable cause) {
		super(message, cause);
	}

	public POSListenerException(String message) {
		super(message);
	}

	public POSListenerException(Throwable cause) {
		super(cause);
	}

	@Override
	public String getMessageDefault() {
		return "BackgroundTaskException - " + (getCause() != null? getCause().getMessage() : "");
	}

}