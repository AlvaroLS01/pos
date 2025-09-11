package com.comerzzia.pos.gui.ventas.tickets.pagos;


public class NoCerrarPantallaException extends com.comerzzia.pos.util.exception.Exception{

	private static final long serialVersionUID = 8544955571810957865L;

	public NoCerrarPantallaException() {
    }

    public NoCerrarPantallaException(String message) {
        super(message);
    }

    public NoCerrarPantallaException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoCerrarPantallaException(Throwable cause) {
        super(cause);
    }

    public NoCerrarPantallaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

	@Override
    public String getMessageDefault() {
	    return null;
    }

}
