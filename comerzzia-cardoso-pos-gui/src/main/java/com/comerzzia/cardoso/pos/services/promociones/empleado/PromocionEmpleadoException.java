package com.comerzzia.cardoso.pos.services.promociones.empleado;

public class PromocionEmpleadoException extends com.comerzzia.pos.util.exception.Exception {

	protected static final long serialVersionUID = 1L;

	public PromocionEmpleadoException() {
	}

	public PromocionEmpleadoException(String message) {
		super(message);
	}

	public PromocionEmpleadoException(String message, Throwable cause) {
		super(message, cause);
	}

	public PromocionEmpleadoException(Throwable cause) {
		super(cause);
	}

	public PromocionEmpleadoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	@Override
	public String getMessageDefault() {
		return null;
	}

}