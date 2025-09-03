package com.comerzzia.iskaypet.pos.gui.mantenimientos.fidelizados.datosgenerales.lenguaje;

import com.comerzzia.pos.util.i18n.I18N;

@SuppressWarnings("serial")
public class LenguajeNotFoundException extends com.comerzzia.pos.util.exception.Exception{

    public LenguajeNotFoundException() {
    }

    public LenguajeNotFoundException(String message) {
        super(message);
    }

    public LenguajeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public LenguajeNotFoundException(Throwable cause) {
        super(cause);
    }

    public LenguajeNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
    @Override
    public String getMessageDefault() {
    	return I18N.getTexto("No se ha encontrado ningún idioma.");
    }
    
}
