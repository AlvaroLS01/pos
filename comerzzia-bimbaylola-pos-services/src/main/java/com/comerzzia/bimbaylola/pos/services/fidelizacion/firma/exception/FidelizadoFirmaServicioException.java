package com.comerzzia.bimbaylola.pos.services.fidelizacion.firma.exception;

import com.comerzzia.pos.util.i18n.I18N;
import com.comerzzia.pos.util.exception.Exception;

@SuppressWarnings("serial")
public class FidelizadoFirmaServicioException extends Exception{

	public FidelizadoFirmaServicioException(){
    }

    public FidelizadoFirmaServicioException(String message){
        super(message);
    }

    public FidelizadoFirmaServicioException(String message, Throwable cause){
        super(message, cause);
    }

    public FidelizadoFirmaServicioException(Throwable cause){
        super(cause);
    }

    public FidelizadoFirmaServicioException(String message, Throwable cause,
    		boolean enableSuppression, boolean writableStackTrace){
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public String getMessageDefault(){
    	return I18N.getTexto("Error al realizar la acción con un FidelizadoFirma");
    }
    
}
