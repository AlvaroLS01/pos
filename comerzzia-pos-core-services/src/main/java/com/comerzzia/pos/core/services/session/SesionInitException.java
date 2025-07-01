


package com.comerzzia.pos.core.services.session;

import com.comerzzia.core.commons.exception.BusinessException;
import com.comerzzia.pos.util.i18n.I18N;


public class SesionInitException extends BusinessException {
    protected static final long serialVersionUID = 1L;

    public SesionInitException(String message) {
        super(message);
    }

    public SesionInitException(String message, Throwable cause) {
        super(message, cause);
    }

    public SesionInitException(Throwable cause) {
        super(cause);
    }

    public SesionInitException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public String getMessageDefault() {
    	return I18N.getText("Error inicializando sesión");
    }
    
    
    
}
