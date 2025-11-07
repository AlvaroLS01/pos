package com.comerzzia.api.loyalty.service.collectivesTypes;

public class TipoColectivoConstraintViolationException extends RuntimeException {

	private static final long serialVersionUID = 4740769220586836417L;

	public TipoColectivoConstraintViolationException() {
	}

	public TipoColectivoConstraintViolationException(String msg) {
		super(msg);
	}

	public TipoColectivoConstraintViolationException(String msg, Throwable e) {
		super(msg, e);
	}
}
