package com.comerzzia.api.loyalty.service.customers.contacts;

public class TiposContactoFidelizadoConstraintViolationException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8703143425422687348L;

	public TiposContactoFidelizadoConstraintViolationException() {
	}

	public TiposContactoFidelizadoConstraintViolationException(String msg) {
		super(msg);
	}

	public TiposContactoFidelizadoConstraintViolationException(String msg, Throwable e) {
		super(msg, e);
	}
}
