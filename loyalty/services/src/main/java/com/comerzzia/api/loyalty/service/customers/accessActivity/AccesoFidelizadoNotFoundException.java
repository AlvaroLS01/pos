package com.comerzzia.api.loyalty.service.customers.accessActivity;

import com.comerzzia.core.util.base.Exception;


public class AccesoFidelizadoNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1244188332063578153L;

	public AccesoFidelizadoNotFoundException() {
	}

	public AccesoFidelizadoNotFoundException(String msg) {
		super(msg);
	}

	public AccesoFidelizadoNotFoundException(String msg, Throwable e) {
		super(msg, e);
	}

	public AccesoFidelizadoNotFoundException(String msg, String msgKey) {
		super(msg, msgKey);
	}

	public AccesoFidelizadoNotFoundException(String msg, String msgKey, Throwable e) {
		super(msg, msgKey, e);
	}

}