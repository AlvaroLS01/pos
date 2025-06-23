package com.comerzzia.bimbaylola.pos.services.movimientos.exception;

import com.comerzzia.core.util.base.Exception;

@SuppressWarnings("serial")
public class CajaMovimientoTarjetaConstraintViolationException extends Exception{

	public CajaMovimientoTarjetaConstraintViolationException(){
		super();
	}
	
	public CajaMovimientoTarjetaConstraintViolationException(String msg){
		super(msg);
	}
	
	public CajaMovimientoTarjetaConstraintViolationException(String msg, Throwable e){
        super(msg, e);
    }

	public CajaMovimientoTarjetaConstraintViolationException(String msg, String msgKey, Throwable e){
		super(msg, msgKey, e);
	}

	public CajaMovimientoTarjetaConstraintViolationException(String msg, String msgKey){
		super(msg, msgKey);
	}
	
}
