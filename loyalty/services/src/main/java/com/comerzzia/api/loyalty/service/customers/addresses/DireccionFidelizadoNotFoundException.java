package com.comerzzia.api.loyalty.service.customers.addresses;

import com.comerzzia.core.util.base.Exception;

public class DireccionFidelizadoNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2528391864261114227L;

	public DireccionFidelizadoNotFoundException() {
	}

	public DireccionFidelizadoNotFoundException(String msg) {
		super(msg);
	}

	public DireccionFidelizadoNotFoundException(String msg, Throwable e) {
		super(msg, e);
	}

	public DireccionFidelizadoNotFoundException(String msg, String msgKey) {
		super(msg, msgKey);
	}

	public DireccionFidelizadoNotFoundException(String msg, String msgKey,
			Throwable e) {
		super(msg, msgKey, e);
	}
}