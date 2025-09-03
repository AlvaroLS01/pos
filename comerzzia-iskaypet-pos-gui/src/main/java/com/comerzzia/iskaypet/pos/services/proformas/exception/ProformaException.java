package com.comerzzia.iskaypet.pos.services.proformas.exception;

import com.comerzzia.pos.util.i18n.I18N;

public class ProformaException extends com.comerzzia.pos.util.exception.Exception {

    private static final long serialVersionUID = 4089764871076823768L;

    public ProformaException() {
    }

    public ProformaException(String message) {
        super(message);
    }

    public ProformaException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProformaException(Throwable cause) {
        super(cause);
    }

    public ProformaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public String getMessageDefault() {
        return I18N.getTexto("Error proforma");
    }

}
