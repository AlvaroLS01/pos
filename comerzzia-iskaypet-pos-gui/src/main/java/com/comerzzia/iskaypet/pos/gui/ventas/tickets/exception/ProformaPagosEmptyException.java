package com.comerzzia.iskaypet.pos.gui.ventas.tickets.exception;

import com.comerzzia.pos.util.i18n.I18N;

public class ProformaPagosEmptyException extends com.comerzzia.pos.util.exception.Exception {

	private static final long serialVersionUID = 4089764871076823768L;

	public ProformaPagosEmptyException() {
    }

    public ProformaPagosEmptyException(String message) {
        super(message);
    }

    public ProformaPagosEmptyException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProformaPagosEmptyException(Throwable cause) {
        super(cause);
    }

    public ProformaPagosEmptyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public String getMessageDefault() {
    	return I18N.getTexto("No existen lineas para la proforma seleccionada");
    }

}
