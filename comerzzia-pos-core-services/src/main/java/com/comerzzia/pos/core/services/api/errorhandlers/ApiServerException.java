package com.comerzzia.pos.core.services.api.errorhandlers;

public class ApiServerException extends ApiException {
	private static final long serialVersionUID = 7874813290269512005L;

	public ApiServerException(ErrorResponse errorResponse) {
		super(errorResponse);
	}	
}
