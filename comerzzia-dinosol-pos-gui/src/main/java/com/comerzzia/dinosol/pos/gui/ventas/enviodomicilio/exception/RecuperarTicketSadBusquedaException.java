package com.comerzzia.dinosol.pos.gui.ventas.enviodomicilio.exception;

/**
 * Controla los errores que se provocan al recuperar el ticket.
 */
@SuppressWarnings("serial")
public class RecuperarTicketSadBusquedaException extends Exception {

	public RecuperarTicketSadBusquedaException() {
	}

	public RecuperarTicketSadBusquedaException(String message) {
		super(message);
	}

	public RecuperarTicketSadBusquedaException(String message, Throwable cause) {
		super(message, cause);
	}

	public RecuperarTicketSadBusquedaException(Throwable cause) {
		super(cause);
	}

	public RecuperarTicketSadBusquedaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
