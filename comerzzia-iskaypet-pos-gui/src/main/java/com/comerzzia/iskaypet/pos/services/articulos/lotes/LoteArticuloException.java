package com.comerzzia.iskaypet.pos.services.articulos.lotes;

import com.comerzzia.pos.util.i18n.I18N;

public class LoteArticuloException extends com.comerzzia.pos.util.exception.Exception {

	private static final long serialVersionUID = 4089764871076823768L;
	
	public LoteArticuloException() {
    }

    public LoteArticuloException(String message) {
        super(message);
    }

    public LoteArticuloException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoteArticuloException(Throwable cause) {
        super(cause);
    }

    public LoteArticuloException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public String getMessageDefault() {
    	return I18N.getTexto("Error procesando lotes");
    }

}
