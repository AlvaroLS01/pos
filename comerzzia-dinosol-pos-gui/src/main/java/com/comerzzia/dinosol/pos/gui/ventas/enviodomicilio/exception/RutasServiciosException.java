package com.comerzzia.dinosol.pos.gui.ventas.enviodomicilio.exception;

/**
 * Controla los errores provocados por los servicios de Rutas.
 */
@SuppressWarnings("serial")
public class RutasServiciosException extends Exception {

	public RutasServiciosException() {
	}

	public RutasServiciosException(String message) {
		super(message);
	}

	public RutasServiciosException(String message, Throwable cause) {
		super(message, cause);
	}

	public RutasServiciosException(Throwable cause) {
		super(cause);
	}

	public RutasServiciosException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}