package com.comerzzia.bimbaylola.pos.services.pais.provincias.exception;

import com.comerzzia.core.util.base.Exception;

@SuppressWarnings("serial")
public class ProvinciasException extends Exception{

	public ProvinciasException(){
		super();
	}
	
	public ProvinciasException(String msg){
		super(msg);
	}
	
	public ProvinciasException(String msg, Throwable e){
        super(msg, e);
    }

	public ProvinciasException(String msg, String msgKey, Throwable e){
		super(msg, msgKey, e);
	}

	public ProvinciasException(String msg, String msgKey){
		super(msg, msgKey);
	}
	
}
