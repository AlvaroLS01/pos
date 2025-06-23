package com.comerzzia.bimbaylola.pos.services.fidelizacion.firma.exception;

import com.comerzzia.pos.util.exception.Exception;
import com.comerzzia.pos.util.i18n.I18N;

@SuppressWarnings("serial")
public class FidelizadoFirmaNotFoundException extends Exception{

	public FidelizadoFirmaNotFoundException(){
    }

    public FidelizadoFirmaNotFoundException(String message){
        super(message);
    }

    public FidelizadoFirmaNotFoundException(String message, Throwable cause){
        super(message, cause);
    }

    public FidelizadoFirmaNotFoundException(Throwable cause){
        super(cause);
    }

    public FidelizadoFirmaNotFoundException(String message, Throwable cause,
    		boolean enableSuppression, boolean writableStackTrace){
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public String getMessageDefault(){
    	return I18N.getTexto("No se han encontrado resultados de Firmas de Fidelizado con estos datos.");
    }
    
}
