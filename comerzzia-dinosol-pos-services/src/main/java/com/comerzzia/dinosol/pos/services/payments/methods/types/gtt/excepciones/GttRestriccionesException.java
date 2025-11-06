package com.comerzzia.dinosol.pos.services.payments.methods.types.gtt.excepciones;

public class GttRestriccionesException extends Exception {

	/**
     * 
     */
    private static final long serialVersionUID = -2341764653302794548L;

	public GttRestriccionesException(Throwable e) {
		super(e);
	}

	public GttRestriccionesException(String message) {
		super(message);
	}

}
