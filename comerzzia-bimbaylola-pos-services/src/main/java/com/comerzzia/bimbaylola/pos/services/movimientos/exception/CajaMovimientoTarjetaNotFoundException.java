package com.comerzzia.bimbaylola.pos.services.movimientos.exception;

import com.comerzzia.core.util.base.Exception;

@SuppressWarnings("serial")
public class CajaMovimientoTarjetaNotFoundException extends Exception{

	public CajaMovimientoTarjetaNotFoundException(){
		super();
	}
	
	public CajaMovimientoTarjetaNotFoundException(String msg){
		super(msg);
	}
	
	public CajaMovimientoTarjetaNotFoundException(String msg, Throwable e){
        super(msg, e);
    }

	public CajaMovimientoTarjetaNotFoundException(String msg, String msgKey, Throwable e){
		super(msg, msgKey, e);
	}

	public CajaMovimientoTarjetaNotFoundException(String msg, String msgKey){
		super(msg, msgKey);
	}
	
}
