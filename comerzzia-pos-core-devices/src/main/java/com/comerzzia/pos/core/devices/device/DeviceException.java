


package com.comerzzia.pos.core.devices.device;

import com.comerzzia.core.commons.exception.BusinessException;
import com.comerzzia.pos.util.i18n.I18N;

public class DeviceException extends BusinessException {
    protected static final long serialVersionUID = 1L;

    public DeviceException() {
    }

    public DeviceException(String message) {
        super(message);
    }

    public DeviceException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeviceException(Throwable cause) {
        super(cause);
    }

    public DeviceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
    @Override
    public String getMessageDefault() {
    	return I18N.getText("Error interactuando con dispositivo externo");
    }

}
