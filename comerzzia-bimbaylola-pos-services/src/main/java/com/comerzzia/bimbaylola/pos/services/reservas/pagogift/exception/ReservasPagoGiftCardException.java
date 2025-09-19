package com.comerzzia.bimbaylola.pos.services.reservas.pagogift.exception;

import com.comerzzia.core.util.base.Exception;

@SuppressWarnings("serial")
public class ReservasPagoGiftCardException extends Exception{

	public ReservasPagoGiftCardException(){
		super();
	}
	
	public ReservasPagoGiftCardException(String msg){
		super(msg);
	}
	
	public ReservasPagoGiftCardException(String msg, Throwable e){
        super(msg, e);
    }

	public ReservasPagoGiftCardException(String msg, String msgKey, Throwable e){
		super(msg, msgKey, e);
	}

	public ReservasPagoGiftCardException(String msg, String msgKey){
		super(msg, msgKey);
	}
	
}
