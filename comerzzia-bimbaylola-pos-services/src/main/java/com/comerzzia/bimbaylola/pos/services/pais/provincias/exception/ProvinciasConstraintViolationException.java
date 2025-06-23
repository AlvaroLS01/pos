package com.comerzzia.bimbaylola.pos.services.pais.provincias.exception;

import com.comerzzia.core.util.base.Exception;

@SuppressWarnings("serial")
public class ProvinciasConstraintViolationException extends Exception{

	public ProvinciasConstraintViolationException(){
		super();
	}
	
	public ProvinciasConstraintViolationException(String msg){
		super(msg);
	}
	
	public ProvinciasConstraintViolationException(String msg, Throwable e){
        super(msg, e);
    }

	public ProvinciasConstraintViolationException(String msg, String msgKey, Throwable e){
		super(msg, msgKey, e);
	}

	public ProvinciasConstraintViolationException(String msg, String msgKey){
		super(msg, msgKey);
	}
	
}
