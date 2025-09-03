package com.comerzzia.iskaypet.pos.devices.tarjeta.sipay.exception;

import com.comerzzia.pos.util.i18n.I18N;

public class SipayException extends com.comerzzia.pos.util.exception.Exception {

	private static final long serialVersionUID = -3352329567501881403L;

	public SipayException() {
	}

	public SipayException(String message) {
		super(message);
	}

	public SipayException(String message, Throwable cause) {
		super(message, cause);
	}

	public SipayException(Throwable cause) {
		super(cause);
	}

	public SipayException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	@Override
	public String getMessageDefault() {
		return I18N.getTexto("Error realizando operación con el TEF de Sipay");
	}

}
