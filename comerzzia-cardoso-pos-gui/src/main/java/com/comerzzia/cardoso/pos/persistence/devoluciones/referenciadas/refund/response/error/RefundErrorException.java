package com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas.refund.response.error;


public class RefundErrorException extends Exception{

	private static final long serialVersionUID = -6050109752774859721L;

	public RefundErrorException() {
	}

	public RefundErrorException(String msg) {
		super(msg);
	}

	public RefundErrorException(String msg, Throwable e) {
		super(msg, e);
	}
}
