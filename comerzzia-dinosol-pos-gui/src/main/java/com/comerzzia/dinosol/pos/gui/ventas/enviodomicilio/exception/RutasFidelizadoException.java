package com.comerzzia.dinosol.pos.gui.ventas.enviodomicilio.exception;

/**
 * Controla los errores relacionados con los datos del fidelizado.
 */
@SuppressWarnings("serial")
public class RutasFidelizadoException extends Exception {

	public RutasFidelizadoException() {
	}

	public RutasFidelizadoException(String message) {
		super(message);
	}

	public RutasFidelizadoException(String message, Throwable cause) {
		super(message, cause);
	}

	public RutasFidelizadoException(Throwable cause) {
		super(cause);
	}

	public RutasFidelizadoException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}