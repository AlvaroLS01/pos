
package com.comerzzia.pos.core.gui.exception;

import com.comerzzia.core.commons.exception.BusinessException;
import com.comerzzia.pos.util.i18n.I18N;


public class LoadWindowException extends BusinessException{

    /**
	 * 
	 */
	private static final long serialVersionUID = -2304650494264821607L;

	public  LoadWindowException() {
        super();
    }
    
    public  LoadWindowException(String msg) {
        super(msg);
    }
    
    public  LoadWindowException(String msg, Exception e) {
        super(msg, e);
    }
    
    public LoadWindowException(Throwable cause) {
        super(cause);
    }

    public LoadWindowException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public String getMessageDefault() {
    	return I18N.getText("Error cargando pantalla. Para mas información consulte el log.");
    }
       
}
