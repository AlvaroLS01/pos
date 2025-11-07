package com.comerzzia.api.loyalty.service.civilStates;

public class EstadoCivilException extends RuntimeException{

	private static final long serialVersionUID = 2791861907612229175L;
	
	public EstadoCivilException() {
	}

	public EstadoCivilException(String msg) {
		super(msg);
	}

	public EstadoCivilException(String msg, Throwable e) {
		super(msg, e);
	}

}
