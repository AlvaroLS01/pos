package com.comerzzia.bimbaylola.pos.services.pais.provincias.exception;

import com.comerzzia.core.util.base.Exception;

@SuppressWarnings("serial")
public class ProvinciasNotFoundException extends Exception{

	public ProvinciasNotFoundException(){
		super();
	}
	
	public ProvinciasNotFoundException(String msg){
		super(msg);
	}
	
	public ProvinciasNotFoundException(String msg, Throwable e){
        super(msg, e);
    }

	public ProvinciasNotFoundException(String msg, String msgKey, Throwable e){
		super(msg, msgKey, e);
	}

	public ProvinciasNotFoundException(String msg, String msgKey){
		super(msg, msgKey);
	}
	
}
