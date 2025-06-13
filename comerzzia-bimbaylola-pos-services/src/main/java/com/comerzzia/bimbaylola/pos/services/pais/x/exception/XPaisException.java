package com.comerzzia.bimbaylola.pos.services.pais.x.exception;

import com.comerzzia.core.util.base.Exception;

@SuppressWarnings("serial")
public class XPaisException extends Exception{

	public XPaisException(){
		super();
	}
	
	public XPaisException(String msg){
		super(msg);
	}
	
	public XPaisException(String msg, Throwable e){
        super(msg, e);
    }

	public XPaisException(String msg, String msgKey, Throwable e){
		super(msg, msgKey, e);
	}

	public XPaisException(String msg, String msgKey){
		super(msg, msgKey);
	}
	
}
