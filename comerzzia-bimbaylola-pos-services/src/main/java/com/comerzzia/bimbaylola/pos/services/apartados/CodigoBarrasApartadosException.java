package com.comerzzia.bimbaylola.pos.services.apartados;

import com.comerzzia.pos.util.i18n.I18N;

@SuppressWarnings("serial")
public class CodigoBarrasApartadosException extends com.comerzzia.pos.util.exception.Exception{
		
	public CodigoBarrasApartadosException(){
    }

    public CodigoBarrasApartadosException(String message){
        super(message);
    }

    public CodigoBarrasApartadosException(String message, Throwable cause){
        super(message, cause);
    }

    public CodigoBarrasApartadosException(Throwable cause){
        super(cause);
    }

    public CodigoBarrasApartadosException(String message, Throwable cause,
    		boolean enableSuppression, boolean writableStackTrace){
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public String getMessageDefault(){
    	return I18N.getTexto("Error al generar el código de barras.");
    }
	
}
