package com.comerzzia.bimbaylola.pos.services.ventas.tipooperacion.exceptions;

import com.comerzzia.pos.util.i18n.I18N;

public class TipoOperacionVentaException extends com.comerzzia.pos.util.exception.Exception {

	private static final long serialVersionUID = -6890010949589856465L;

	public TipoOperacionVentaException() {
	}

	public TipoOperacionVentaException(String message) {
		super(message);
	}

	public TipoOperacionVentaException(String message, Throwable cause) {
		super(message, cause);
	}

	public TipoOperacionVentaException(Throwable cause) {
		super(cause);
	}

	public TipoOperacionVentaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	@Override
	public String getMessageDefault() {
		return I18N.getTexto("Error en la consulta de los tipos de operacion de la venta.");
	}
}
