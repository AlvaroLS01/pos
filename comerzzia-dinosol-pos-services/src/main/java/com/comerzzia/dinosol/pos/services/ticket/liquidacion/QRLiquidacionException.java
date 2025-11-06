package com.comerzzia.dinosol.pos.services.ticket.liquidacion;

import com.comerzzia.pos.util.i18n.I18N;

public class QRLiquidacionException extends com.comerzzia.pos.util.exception.Exception {

	private static final long serialVersionUID = 8100270580624585849L;

	public QRLiquidacionException() {
		super();
	}

	public QRLiquidacionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public QRLiquidacionException(String message, Throwable cause) {
		super(message, cause);
	}

	public QRLiquidacionException(String message) {
		super(message);
	}

	public QRLiquidacionException(Throwable cause) {
		super(cause);
	}

	@Override
	public String getMessageDefault() {
		return I18N.getTexto("Error aplicando codigo de barras especial QR LIQUIDACION");
	}

}
