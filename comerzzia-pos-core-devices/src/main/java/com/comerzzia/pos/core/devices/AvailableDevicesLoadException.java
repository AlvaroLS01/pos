
package com.comerzzia.pos.core.devices;

import java.util.ArrayList;
import java.util.List;

import com.comerzzia.core.commons.exception.BusinessException;
import com.comerzzia.pos.util.i18n.I18N;

public class AvailableDevicesLoadException extends BusinessException {
    private static final long serialVersionUID = 1L;
    private List<String> errors = new ArrayList<String>();
    
    public AvailableDevicesLoadException(List<String> errors) {
    	this.errors.addAll(errors);
    }

    public AvailableDevicesLoadException(String message) {
        super(message);
        errors.add(message);
    }

    public AvailableDevicesLoadException(String message, Throwable cause) {
        super(message, cause);
        errors.add(message);
    }

    public AvailableDevicesLoadException(Throwable cause) {
        super(cause);
        errors.add(cause.getMessage());
    }

    public AvailableDevicesLoadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        errors.add(message);
    }
    
    public List<String> getErrors() {
    	return errors;
    }

    @Override
    public String getMessageDefault() {
    	return I18N.getText("Error leyendo XML de configuración de dispositivos");
    }

    
}
