package com.comerzzia.dinosol.pos.services.payments.methods.types.siam.dto.errores;

public class SiamException extends Exception {

	private static final long serialVersionUID = 1L;
	private SiamError error;

	public SiamException() {
	}

	public SiamException(String msg, SiamError error) {
		super(msg);
		this.setError(error);
	}

	public SiamException(String msg, Throwable e) {
		super(msg, e);
	}

	public SiamError getError() {
		return error;
	}

	public void setError(SiamError error) {
		this.error = error;
	}
	
}
