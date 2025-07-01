package com.comerzzia.pos.core.services.api.errorhandlers;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import feign.Response;
import feign.codec.ErrorDecoder;


public class ApiErrorDecoder implements ErrorDecoder {
	
	private final ErrorDecoder errorDecoder = new Default();

	@Override
    public Exception decode(String methodKey, Response response) {
		ErrorResponse error = null;
		
		try {						
			if (response.body() != null) {
				error = new ObjectMapper().readValue(response.body().asInputStream(), ErrorResponse.class);
			}
		}
		catch (IOException ignored) { // NOPMD
		}
		
		if (error != null) {
			if (response.status() >= 400 && response.status() <= 499) {
	            return new ApiClientException(error);
	        }
	        if (response.status() >= 500 && response.status() <= 599) {
	        	return new ApiServerException(error);
	        }	
		}		
        
        return errorDecoder.decode(methodKey, response);
    }
	
	
}
