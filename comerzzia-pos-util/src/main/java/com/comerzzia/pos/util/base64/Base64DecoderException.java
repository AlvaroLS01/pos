package com.comerzzia.pos.util.base64;

public class Base64DecoderException extends Exception {
    
	private static final long serialVersionUID = -8869607014938535614L;

	public Base64DecoderException() {
    }
        
    public Base64DecoderException(String msg) {
        super(msg);
    }

    public Base64DecoderException(String msg,Throwable e) {
        super(msg,e);
    }

    public Base64DecoderException(Throwable e) {
        super(e);
    }
}