package com.comerzzia.dinosol.pos.gui.ventas.enviodomicilio.exception;

/**
 * Controla los errores producidos por la petición de Token.
 */
@SuppressWarnings("serial")
public class RutasTokenException extends Exception {

	public RutasTokenException() {
	}

	public RutasTokenException(String message) {
		super(message);
	}

	public RutasTokenException(String message, Throwable cause) {
		super(message, cause);
	}

	public RutasTokenException(Throwable cause) {
		super(cause);
	}

	public RutasTokenException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
