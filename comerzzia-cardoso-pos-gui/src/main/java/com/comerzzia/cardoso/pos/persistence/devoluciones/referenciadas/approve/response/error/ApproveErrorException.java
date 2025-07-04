package com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas.approve.response.error;


public class ApproveErrorException  extends Exception{

	private static final long serialVersionUID = -6050109752774859721L;

	public ApproveErrorException() {
	}

	public ApproveErrorException(String msg) {
		super(msg);
	}

	public ApproveErrorException(String msg, Throwable e) {
		super(msg, e);
	}
}
