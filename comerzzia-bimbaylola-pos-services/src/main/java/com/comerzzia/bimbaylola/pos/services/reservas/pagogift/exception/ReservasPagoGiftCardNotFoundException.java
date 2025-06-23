package com.comerzzia.bimbaylola.pos.services.reservas.pagogift.exception;

import com.comerzzia.core.util.base.Exception;

@SuppressWarnings("serial")
public class ReservasPagoGiftCardNotFoundException extends Exception{

	public ReservasPagoGiftCardNotFoundException(){
		super();
	}
	
	public ReservasPagoGiftCardNotFoundException(String msg){
		super(msg);
	}
	
	public ReservasPagoGiftCardNotFoundException(String msg, Throwable e){
        super(msg, e);
    }

	public ReservasPagoGiftCardNotFoundException(String msg, String msgKey, Throwable e){
		super(msg, msgKey, e);
	}

	public ReservasPagoGiftCardNotFoundException(String msg, String msgKey){
		super(msg, msgKey);
	}
	
}
