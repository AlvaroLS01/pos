package com.comerzzia.cardoso.pos.services.tarifas.exception;

/**
 * GAP - DESCUENTO TARIFA
 */
@SuppressWarnings("serial")
public class TarifaDetalleException extends Exception{

	public TarifaDetalleException(){}

	public TarifaDetalleException(String message){
		super(message);
	}

	public TarifaDetalleException(String message, Throwable cause){
		super(message, cause);
	}

	public TarifaDetalleException(Throwable cause){
		super(cause);
	}

	public TarifaDetalleException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace){
		super(message, cause, enableSuppression, writableStackTrace);
	}

}

