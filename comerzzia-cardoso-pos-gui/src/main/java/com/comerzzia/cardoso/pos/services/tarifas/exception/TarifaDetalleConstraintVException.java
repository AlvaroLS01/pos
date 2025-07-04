package com.comerzzia.cardoso.pos.services.tarifas.exception;

/**
 * GAP - DESCUENTO TARIFA
 */
@SuppressWarnings("serial")
public class TarifaDetalleConstraintVException extends Exception{

	public TarifaDetalleConstraintVException(){}

	public TarifaDetalleConstraintVException(String message){
		super(message);
	}

	public TarifaDetalleConstraintVException(String message, Throwable cause){
		super(message, cause);
	}

	public TarifaDetalleConstraintVException(Throwable cause){
		super(cause);
	}

	public TarifaDetalleConstraintVException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace){
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
