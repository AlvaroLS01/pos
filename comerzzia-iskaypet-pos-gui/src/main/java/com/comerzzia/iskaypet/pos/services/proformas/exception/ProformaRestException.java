package com.comerzzia.iskaypet.pos.services.proformas.exception;

import com.comerzzia.pos.util.i18n.I18N;

public class ProformaRestException extends com.comerzzia.pos.util.exception.Exception {

    private static final long serialVersionUID = 4089764871076823768L;

    public ProformaRestException() {
    }

    public ProformaRestException(String message) {
        super(message);
    }

    public ProformaRestException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProformaRestException(Throwable cause) {
        super(cause);
    }

    public ProformaRestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public String getMessageDefault() {
        return I18N.getTexto("Error buscndo proformas");
    }

}
