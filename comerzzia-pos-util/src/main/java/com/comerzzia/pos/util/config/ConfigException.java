


package com.comerzzia.pos.util.config;

import com.comerzzia.core.commons.exception.BusinessException;
import com.comerzzia.pos.util.i18n.I18N;


public class ConfigException extends BusinessException {
    
    private static final long serialVersionUID = 1L;

    public ConfigException(String message) {
        super(message);
    }

    public ConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigException(Throwable cause) {
        super(cause);
    }

    public ConfigException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public String getMessageDefault() {
    	return I18N.getText("Error cargando fichero de configuración del TPV");
    }
    
}
