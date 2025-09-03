package com.comerzzia.iskaypet.pos.services.valesdevolucion.exception;

import com.comerzzia.pos.util.i18n.I18N;

public class ValesFormatExcepcion  extends com.comerzzia.pos.util.exception.Exception{


    @Override
    public String getMessageDefault() {
        return I18N.getTexto("Vale incorrecto");
    }
}
