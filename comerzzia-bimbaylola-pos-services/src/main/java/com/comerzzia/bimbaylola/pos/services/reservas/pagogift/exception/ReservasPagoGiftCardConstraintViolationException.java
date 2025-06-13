package com.comerzzia.bimbaylola.pos.services.reservas.pagogift.exception;

import com.comerzzia.core.util.base.Exception;

@SuppressWarnings("serial")
public class ReservasPagoGiftCardConstraintViolationException extends Exception{

	public ReservasPagoGiftCardConstraintViolationException(){
		super();
	}
	
	public ReservasPagoGiftCardConstraintViolationException(String msg){
		super(msg);
	}
	
	public ReservasPagoGiftCardConstraintViolationException(String msg, Throwable e){
        super(msg, e);
    }

	public ReservasPagoGiftCardConstraintViolationException(String msg, String msgKey, Throwable e){
		super(msg, msgKey, e);
	}

	public ReservasPagoGiftCardConstraintViolationException(String msg, String msgKey){
		super(msg, msgKey);
	}
	
}
