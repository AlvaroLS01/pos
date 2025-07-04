package com.comerzzia.cardoso.pos.gui.promociones.monograficas.exception;

/**
 * GAP - PERSONALIZACIONES V3 - PROMOCIONES MONOGRÁFICAS
 */
@SuppressWarnings("serial")
public class PromocionMonograficaException extends Exception{

	public PromocionMonograficaException(){
	}

	public PromocionMonograficaException(String message){
		super(message);
	}

	public PromocionMonograficaException(String message, Throwable cause){
		super(message, cause);
	}

	public PromocionMonograficaException(Throwable cause){
		super(cause);
	}

	public PromocionMonograficaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace){
		super(message, cause, enableSuppression, writableStackTrace);
	}

}