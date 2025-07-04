package com.comerzzia.cardoso.pos.services.balanza.exception;

/**
 * GAP - PERSONALIZACIONES V3 - INTEGRACIÓN BALANZA BIZERBA
 */
@SuppressWarnings("serial")
public class TicketBalanzaServiceException extends Exception{

	public TicketBalanzaServiceException(){}

	public TicketBalanzaServiceException(String message){
		super(message);
	}

	public TicketBalanzaServiceException(String message, Throwable cause){
		super(message, cause);
	}

	public TicketBalanzaServiceException(Throwable cause){
		super(cause);
	}

	public TicketBalanzaServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace){
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
