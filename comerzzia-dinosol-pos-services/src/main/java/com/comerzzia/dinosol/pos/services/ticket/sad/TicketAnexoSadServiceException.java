package com.comerzzia.dinosol.pos.services.ticket.sad;

import com.comerzzia.pos.util.i18n.I18N;


public class TicketAnexoSadServiceException extends com.comerzzia.pos.util.exception.Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -1721975511432547342L;

	public TicketAnexoSadServiceException() {
    }

    public TicketAnexoSadServiceException(String message) {
        super(message);
    }

    public TicketAnexoSadServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public TicketAnexoSadServiceException(Throwable cause) {
        super(cause);
    }

    public TicketAnexoSadServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public String getMessageDefault() {
    	return I18N.getTexto("Error procesando la tabla anexa de tickets");
    }
}
