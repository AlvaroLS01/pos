


package com.comerzzia.pos.core.gui.exception;

import com.comerzzia.core.commons.exception.BusinessException;
import com.comerzzia.pos.util.i18n.I18N;


public class ValidationException extends BusinessException {

     /**
	 * 
	 */
	private static final long serialVersionUID = -2071762330697441363L;

	public  ValidationException() {
        super();
    }
    
    public  ValidationException(String msg) {
        super(msg);
    }
    
    public  ValidationException(String msg, Exception e) {
        super(msg, e);
    }
    
    public ValidationException(Throwable cause) {
        super(cause);
    }

    public ValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
    @Override
    public String getMessageDefault() {
    	return I18N.getText("Error de validación");
    }
    
}
