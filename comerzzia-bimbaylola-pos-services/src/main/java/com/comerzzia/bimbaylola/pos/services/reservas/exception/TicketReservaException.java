package com.comerzzia.bimbaylola.pos.services.reservas.exception;

@SuppressWarnings("serial")
public class TicketReservaException extends Exception{

	public TicketReservaException(){
	}

	public TicketReservaException(String message){
		super(message);
	}

	public TicketReservaException(String message, Throwable cause){
		super(message, cause);
	}

	public TicketReservaException(Throwable cause){
		super(cause);
	}

	public TicketReservaException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace){
		super(message, cause, enableSuppression, writableStackTrace);
	}

}