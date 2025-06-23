package com.comerzzia.bimbaylola.pos.services.articulos.articulosNoAptos;

import com.comerzzia.core.util.base.Exception;

public class ArticuloNoAptoException extends Exception{

	private static final long serialVersionUID = 6267250208256555046L;

	public ArticuloNoAptoException(){
		super();
	}
	
	public ArticuloNoAptoException(String msg){
		super(msg);
	}
	
	public ArticuloNoAptoException(String msg, Throwable e){
        super(msg, e);
    }

	public ArticuloNoAptoException(String msg, String msgKey, Throwable e){
		super(msg, msgKey, e);
	}

	public ArticuloNoAptoException(String msg, String msgKey){
		super(msg, msgKey);
	}
	
}
