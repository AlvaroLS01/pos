package com.comerzzia.dinosol.pos.services.ticket.aparcados;

public class TicketsMaximoAparcadosException extends Exception{
	  /**
     * 
     */
    private static final long serialVersionUID = -1721975511432547342L;

	public TicketsMaximoAparcadosException() {
    }

    public TicketsMaximoAparcadosException(String message) {
        super(message);
    }

    public TicketsMaximoAparcadosException(String message, Throwable cause) {
        super(message, cause);
    }

    public TicketsMaximoAparcadosException(Throwable cause) {
        super(cause);
    }

    public TicketsMaximoAparcadosException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
