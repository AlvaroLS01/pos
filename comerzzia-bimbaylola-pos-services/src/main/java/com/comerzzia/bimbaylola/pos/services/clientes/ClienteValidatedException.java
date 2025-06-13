package com.comerzzia.bimbaylola.pos.services.clientes;

import com.comerzzia.pos.util.i18n.I18N;

@SuppressWarnings("serial")
public class ClienteValidatedException extends com.comerzzia.pos.util.exception.Exception{

	public ClienteValidatedException(){
    }

    public ClienteValidatedException(String message){
        super(message);
    }

    public ClienteValidatedException(String message, Throwable cause){
        super(message, cause);
    }

    public ClienteValidatedException(Throwable cause){
        super(cause);
    }

    public ClienteValidatedException(String message, Throwable cause,
    		boolean enableSuppression, boolean writableStackTrace){
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public String getMessageDefault(){
    	return I18N.getTexto("Error en la validación del documento.");
    }
    
}
