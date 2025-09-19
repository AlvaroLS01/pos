package com.comerzzia.bimbaylola.pos.services.ticket.aparcados.reservas.exception;

import com.comerzzia.core.util.base.Exception;

@SuppressWarnings("serial")
public class TicketReservaException extends Exception{

	public TicketReservaException(){
		super();
	}
	public TicketReservaException(String msg){
		super(msg);
	}
	public TicketReservaException(String msg, Throwable e){
        super(msg, e);
    }
	public TicketReservaException(String msg, String msgKey, Throwable e){
		super(msg, msgKey, e);
	}
	public TicketReservaException(String msg, String msgKey){
		super(msg, msgKey);
	}
	
}
