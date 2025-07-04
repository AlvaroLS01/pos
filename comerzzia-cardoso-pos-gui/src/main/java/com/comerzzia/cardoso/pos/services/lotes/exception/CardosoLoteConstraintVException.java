package com.comerzzia.cardoso.pos.services.lotes.exception;

/**
 * GAP - PERSONALIZACIONES V3 - LOTES
 */
@SuppressWarnings("serial")
public class CardosoLoteConstraintVException extends Exception{

	public CardosoLoteConstraintVException(){}

	public CardosoLoteConstraintVException(String message){
		super(message);
	}

	public CardosoLoteConstraintVException(String message, Throwable cause){
		super(message, cause);
	}

	public CardosoLoteConstraintVException(Throwable cause){
		super(cause);
	}

	public CardosoLoteConstraintVException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace){
		super(message, cause, enableSuppression, writableStackTrace);
	}

}

