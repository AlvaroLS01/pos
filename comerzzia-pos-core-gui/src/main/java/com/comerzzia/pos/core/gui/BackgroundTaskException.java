
package com.comerzzia.pos.core.gui;

import com.comerzzia.core.commons.exception.BusinessException;

public class BackgroundTaskException extends BusinessException {

	private static final long serialVersionUID = 1L;

	public BackgroundTaskException() {
		super();
	}

	public BackgroundTaskException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public BackgroundTaskException(String message, Throwable cause) {
		super(message, cause);
	}

	public BackgroundTaskException(String message) {
		super(message);
	}

	public BackgroundTaskException(Throwable cause) {
		super(cause);
	}

	@Override
	public String getMessageDefault() {
		return "BackgroundTaskException - " + (getCause() != null? getCause().getMessage() : "");
	}

}