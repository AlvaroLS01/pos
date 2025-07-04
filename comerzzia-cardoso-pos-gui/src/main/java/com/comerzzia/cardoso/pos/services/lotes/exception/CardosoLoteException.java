package com.comerzzia.cardoso.pos.services.lotes.exception;

/**
 * GAP - PERSONALIZACIONES V3 - LOTES
 */
@SuppressWarnings("serial")
public class CardosoLoteException extends Exception{

	public CardosoLoteException(){}

	public CardosoLoteException(String message){
		super(message);
	}

	public CardosoLoteException(String message, Throwable cause){
		super(message, cause);
	}

	public CardosoLoteException(Throwable cause){
		super(cause);
	}

	public CardosoLoteException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace){
		super(message, cause, enableSuppression, writableStackTrace);
	}

}

