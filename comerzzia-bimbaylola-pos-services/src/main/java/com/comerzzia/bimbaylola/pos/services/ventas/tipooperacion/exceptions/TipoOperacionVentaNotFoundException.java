package com.comerzzia.bimbaylola.pos.services.ventas.tipooperacion.exceptions;

import com.comerzzia.pos.util.i18n.I18N;

public class TipoOperacionVentaNotFoundException extends com.comerzzia.pos.util.exception.Exception {

	private static final long serialVersionUID = 1232313842082432084L;

	public TipoOperacionVentaNotFoundException() {
	}

	public TipoOperacionVentaNotFoundException(String message) {
		super(message);
	}

	public TipoOperacionVentaNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public TipoOperacionVentaNotFoundException(Throwable cause) {
		super(cause);
	}

	public TipoOperacionVentaNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	@Override
	public String getMessageDefault() {
		return I18N.getTexto("No se ha encontrado ningún tipo de operación para la venta.");
	}

}
