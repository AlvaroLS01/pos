package com.comerzzia.cardoso.pos.services.agente.exception;

/**
 * GAP - AGENTES
 */
@SuppressWarnings("serial")
public class ClienteAgenteException extends Exception{

	public ClienteAgenteException(){}

	public ClienteAgenteException(String message){
		super(message);
	}

	public ClienteAgenteException(String message, Throwable cause){
		super(message, cause);
	}

	public ClienteAgenteException(Throwable cause){
		super(cause);
	}

	public ClienteAgenteException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace){
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
