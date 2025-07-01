package com.comerzzia.pos.core.services.api.errorhandlers;

public class ApiClientException extends ApiException {
	private static final long serialVersionUID = -5749715372839277483L;
	
	public ApiClientException(ErrorResponse errorResponse) {
		super(errorResponse);
	}	
}
