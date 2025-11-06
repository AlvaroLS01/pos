package com.comerzzia.dinosol.pos.services.ticket.anexo;

import com.comerzzia.pos.util.i18n.I18N;


public class AnexaTicketsServiceException extends com.comerzzia.pos.util.exception.Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -1721975511432547342L;

	public AnexaTicketsServiceException() {
    }

    public AnexaTicketsServiceException(String message) {
        super(message);
    }

    public AnexaTicketsServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public AnexaTicketsServiceException(Throwable cause) {
        super(cause);
    }

    public AnexaTicketsServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public String getMessageDefault() {
    	return I18N.getTexto("Error procesando la tabla anexa de tickets");
    }
}
