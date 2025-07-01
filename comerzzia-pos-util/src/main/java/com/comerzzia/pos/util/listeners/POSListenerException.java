package com.comerzzia.pos.util.listeners;

import com.comerzzia.core.commons.exception.BusinessException;

public class POSListenerException extends BusinessException {

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