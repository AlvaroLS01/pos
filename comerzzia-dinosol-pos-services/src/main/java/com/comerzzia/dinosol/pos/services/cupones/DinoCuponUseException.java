package com.comerzzia.dinosol.pos.services.cupones;

import com.comerzzia.pos.services.cupones.CuponUseException;
import com.comerzzia.pos.util.i18n.I18N;

public class DinoCuponUseException extends CuponUseException{
	
	private static final long serialVersionUID = 6710817681268372119L;

	 	public DinoCuponUseException() {
	    }

	    public DinoCuponUseException(String message) {
	        super(message);
	    }

	    public DinoCuponUseException(String message, Throwable cause) {
	        super(message, cause);
	    }

	    public DinoCuponUseException(Throwable cause) {
	        super(cause);
	    }

	    public DinoCuponUseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
	        super(message, cause, enableSuppression, writableStackTrace);
	    }

	    @Override
	    public String getMessageDefault() {
	    	return I18N.getTexto("El cupón indicado ya ha sido utilizado en esta compra");
	    }   
}
