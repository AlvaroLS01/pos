package com.comerzzia.api.loyalty.service.cardsTypes;

public class TipoTarjetaConstraintViolationException extends RuntimeException {

	private static final long serialVersionUID = 3649001899373702997L;

	public TipoTarjetaConstraintViolationException() {
	}

	public TipoTarjetaConstraintViolationException(String msg) {
		super(msg);
	}

	public TipoTarjetaConstraintViolationException(String msg, Throwable e) {
		super(msg, e);
	}
}
