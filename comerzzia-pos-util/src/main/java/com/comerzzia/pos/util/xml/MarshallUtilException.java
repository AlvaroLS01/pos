
package com.comerzzia.pos.util.xml;

import com.comerzzia.core.commons.exception.BusinessException;
import com.comerzzia.pos.util.i18n.I18N;


public class MarshallUtilException extends BusinessException {
    private static final long serialVersionUID = 1L;

    public MarshallUtilException() {
    }

    public MarshallUtilException(String message) {
        super(message);
    }

    public MarshallUtilException(String message, Throwable cause) {
        super(message, cause);
    }

    public MarshallUtilException(Throwable cause) {
        super(cause);
    }

    public MarshallUtilException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    
    
    @Override
    public String getMessageDefault() {
        return I18N.getText("Error en parseo de XML");
    }
    
}
