package com.comerzzia.pos.core.services.api.errorhandlers;

import feign.FeignException;

public abstract class ApiException extends FeignException {
	private static final long serialVersionUID = 2865454718235178686L;
	private ErrorResponse errorResponse;

	public ApiException(ErrorResponse errorResponse) {
	   super(500, errorResponse.getMessage());
	   this.errorResponse = errorResponse;	   
	}
	
	public ErrorResponse getErrorResponse() {
		return this.errorResponse;
	}
}
