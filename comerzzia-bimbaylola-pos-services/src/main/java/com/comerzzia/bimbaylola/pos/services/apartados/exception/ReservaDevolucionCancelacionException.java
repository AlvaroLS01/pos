package com.comerzzia.bimbaylola.pos.services.apartados.exception;

import com.comerzzia.core.util.base.Exception;

@SuppressWarnings("serial")
public class ReservaDevolucionCancelacionException extends Exception{

	public ReservaDevolucionCancelacionException(){
		super();
	}
	
	public ReservaDevolucionCancelacionException(String msg){
		super(msg);
	}
	
	public ReservaDevolucionCancelacionException(String msg, Throwable e){
        super(msg, e);
    }

	public ReservaDevolucionCancelacionException(String msg, String msgKey, Throwable e){
		super(msg, msgKey, e);
	}

	public ReservaDevolucionCancelacionException(String msg, String msgKey){
		super(msg, msgKey);
	}
	
}
