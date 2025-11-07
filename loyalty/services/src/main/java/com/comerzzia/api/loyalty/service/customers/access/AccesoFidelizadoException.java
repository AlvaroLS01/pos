package com.comerzzia.api.loyalty.service.customers.access;


public class AccesoFidelizadoException extends RuntimeException{

	private static final long serialVersionUID = 75599141989051448L;

	public AccesoFidelizadoException() {
	}

	public AccesoFidelizadoException(String msg) {
		super(msg);
	}

	public AccesoFidelizadoException(String msg, Throwable e) {
		super(msg, e);
	}

}