package com.comerzzia.bimbaylola.pos.services.fidelizacion.firma.exception;

import com.comerzzia.pos.util.exception.Exception;
import com.comerzzia.pos.util.i18n.I18N;

@SuppressWarnings("serial")
public class FidelizadoFirmaConstraintViolationException extends Exception{

	public FidelizadoFirmaConstraintViolationException(){
    }

    public FidelizadoFirmaConstraintViolationException(String message){
        super(message);
    }

    public FidelizadoFirmaConstraintViolationException(String message, Throwable cause){
        super(message, cause);
    }

    public FidelizadoFirmaConstraintViolationException(Throwable cause){
        super(cause);
    }

    public FidelizadoFirmaConstraintViolationException(String message, Throwable cause,
    		boolean enableSuppression, boolean writableStackTrace){
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public String getMessageDefault(){
    	return I18N.getTexto("Ya existe un FidelizadoFirma con estos datos.");
    }
    
}
