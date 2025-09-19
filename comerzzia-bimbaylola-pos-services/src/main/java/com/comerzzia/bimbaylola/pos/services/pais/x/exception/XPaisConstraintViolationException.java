package com.comerzzia.bimbaylola.pos.services.pais.x.exception;

import com.comerzzia.core.util.base.Exception;

@SuppressWarnings("serial")
public class XPaisConstraintViolationException extends Exception{

	public XPaisConstraintViolationException(){
		super();
	}
	
	public XPaisConstraintViolationException(String msg){
		super(msg);
	}
	
	public XPaisConstraintViolationException(String msg, Throwable e){
        super(msg, e);
    }

	public XPaisConstraintViolationException(String msg, String msgKey, Throwable e){
		super(msg, msgKey, e);
	}

	public XPaisConstraintViolationException(String msg, String msgKey){
		super(msg, msgKey);
	}
	
}
