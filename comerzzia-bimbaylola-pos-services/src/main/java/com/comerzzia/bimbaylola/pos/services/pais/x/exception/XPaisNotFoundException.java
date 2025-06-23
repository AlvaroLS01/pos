package com.comerzzia.bimbaylola.pos.services.pais.x.exception;

import com.comerzzia.core.util.base.Exception;

@SuppressWarnings("serial")
public class XPaisNotFoundException extends Exception{

	public XPaisNotFoundException(){
		super();
	}
	
	public XPaisNotFoundException(String msg){
		super(msg);
	}
	
	public XPaisNotFoundException(String msg, Throwable e){
        super(msg, e);
    }

	public XPaisNotFoundException(String msg, String msgKey, Throwable e){
		super(msg, msgKey, e);
	}

	public XPaisNotFoundException(String msg, String msgKey){
		super(msg, msgKey);
	}
	
}
