package com.comerzzia.bimbaylola.pos.services.movimientos.exception;

import com.comerzzia.core.util.base.Exception;

@SuppressWarnings("serial")
public class CajaMovimientoTarjetaException extends Exception{

	public CajaMovimientoTarjetaException(){
		super();
	}
	
	public CajaMovimientoTarjetaException(String msg){
		super(msg);
	}
	
	public CajaMovimientoTarjetaException(String msg, Throwable e){
        super(msg, e);
    }

	public CajaMovimientoTarjetaException(String msg, String msgKey, Throwable e){
		super(msg, msgKey, e);
	}

	public CajaMovimientoTarjetaException(String msg, String msgKey){
		super(msg, msgKey);
	}
	
}
