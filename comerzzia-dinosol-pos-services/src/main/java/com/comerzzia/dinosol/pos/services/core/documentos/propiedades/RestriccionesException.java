package com.comerzzia.dinosol.pos.services.core.documentos.propiedades;



@SuppressWarnings("serial")
public class RestriccionesException extends Exception {

	public RestriccionesException() {
    }

    public RestriccionesException(String message) {
        super(message);
    }

    public RestriccionesException(String message, Throwable cause) {
        super(message, cause);
    }

    public RestriccionesException(Throwable cause) {
        super(cause);
    }

    public RestriccionesException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}