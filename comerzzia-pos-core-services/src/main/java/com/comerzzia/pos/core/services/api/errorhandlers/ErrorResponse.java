package com.comerzzia.pos.core.services.api.errorhandlers;


public class ErrorResponse {	
	private String type;
	private String message;	
	
    public String getType() {
    	return type;
    }
	
    public void setType(String type) {
    	this.type = type;
    }
	
    public String getMessage() {
    	return message;
    }
	
    public void setMessage(String message) {
    	this.message = message;
    }
}
