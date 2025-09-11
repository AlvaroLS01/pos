package com.comerzzia.iskaypet.pos.services.valesdevolucion.exception;

import com.comerzzia.pos.util.i18n.I18N;

public class ValesDevolucionNotFoundException extends com.comerzzia.pos.util.exception.Exception {

    /**
     *
     */
    private static final long serialVersionUID = -9140449535109061297L;

    @Override
    public String getMessageDefault() {
        return I18N.getTexto("No se ha encontrado el vale de devolución con el número de tarjeta indicado");
    }

}
