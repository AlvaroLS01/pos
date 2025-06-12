package com.comerzzia.pampling.pos.services.fiscal.italia.exception;

import com.comerzzia.pos.services.ticket.TicketsServiceException;

public class ItaliaFiscalException extends TicketsServiceException {
	private static final long serialVersionUID = 1L;

	public ItaliaFiscalException(String message) {
		super(message);
	}

	public ItaliaFiscalException(String message, Exception e) {
		super(message, e);
	}

}
