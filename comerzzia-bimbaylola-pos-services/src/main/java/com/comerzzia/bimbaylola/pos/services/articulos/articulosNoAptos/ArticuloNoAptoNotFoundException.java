package com.comerzzia.bimbaylola.pos.services.articulos.articulosNoAptos;

import com.comerzzia.core.util.base.Exception;

public class ArticuloNoAptoNotFoundException extends Exception{

	private static final long serialVersionUID = 6267250208256555046L;

	public ArticuloNoAptoNotFoundException(){
		super();
	}
	
	public ArticuloNoAptoNotFoundException(String msg){
		super(msg);
	}
	
	public ArticuloNoAptoNotFoundException(String msg, Throwable e){
        super(msg, e);
    }

	public ArticuloNoAptoNotFoundException(String msg, String msgKey, Throwable e){
		super(msg, msgKey, e);
	}

	public ArticuloNoAptoNotFoundException(String msg, String msgKey){
		super(msg, msgKey);
	}
	
}
