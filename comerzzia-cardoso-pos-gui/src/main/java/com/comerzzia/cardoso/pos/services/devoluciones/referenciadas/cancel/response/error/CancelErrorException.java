package com.comerzzia.cardoso.pos.services.devoluciones.referenciadas.cancel.response.error;


public class CancelErrorException extends Exception {

	private static final long serialVersionUID = -1371496011106669574L;

	public CancelErrorException() {
	}

	public CancelErrorException(String msg) {
		super(msg);
	}

	public CancelErrorException(String msg, Throwable e) {
		super(msg, e);
	}
}
