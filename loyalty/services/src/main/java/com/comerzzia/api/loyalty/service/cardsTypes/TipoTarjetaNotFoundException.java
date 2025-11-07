package com.comerzzia.api.loyalty.service.cardsTypes;

public class TipoTarjetaNotFoundException extends Exception {

	private static final long serialVersionUID = 1478766372264119533L;

	public TipoTarjetaNotFoundException() {
	}

	public TipoTarjetaNotFoundException(String msg) {
		super(msg);
	}

	public TipoTarjetaNotFoundException(String msg, Throwable e) {
		super(msg, e);
	}
}


