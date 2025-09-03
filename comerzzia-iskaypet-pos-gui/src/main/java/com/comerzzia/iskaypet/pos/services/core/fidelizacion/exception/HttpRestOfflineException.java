package com.comerzzia.iskaypet.pos.services.core.fidelizacion.exception;

import com.comerzzia.pos.util.i18n.I18N;

public class HttpRestOfflineException extends com.comerzzia.pos.util.exception.Exception {

    /**
     *
     */
    private static final long serialVersionUID = 6484380833087288688L;

    public HttpRestOfflineException() {
    }

    public HttpRestOfflineException(String message) {
        super(message);
    }

    public HttpRestOfflineException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpRestOfflineException(Throwable cause) {
        super(cause);
    }

    public HttpRestOfflineException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public String getMessageDefault() {
        return I18N.getTexto("No hay conexión con central");
    }

}
