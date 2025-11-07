package com.comerzzia.api.loyalty.service.collectivesTypes;

public class TipoColectivoNotFoundException extends Exception {

	private static final long serialVersionUID = 6270390960864217474L;

	public TipoColectivoNotFoundException() {
	}

	public TipoColectivoNotFoundException(String msg) {
		super(msg);
	}

	public TipoColectivoNotFoundException(String msg, Throwable e) {
		super(msg, e);
	}
}

