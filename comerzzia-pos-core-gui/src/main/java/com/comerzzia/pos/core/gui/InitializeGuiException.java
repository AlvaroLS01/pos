
package com.comerzzia.pos.core.gui;

import com.comerzzia.pos.util.i18n.I18N;

public class InitializeGuiException extends Exception{
    private static final long serialVersionUID = 1L;
	private boolean showError = true;

    public InitializeGuiException() {
    }
    
    public InitializeGuiException(String message, boolean showError) {
    	super(message);
    	this.setShowError(showError);
    }

    public InitializeGuiException(String message, Throwable cause, boolean showError) {
    	super(message,cause);
    	this.setShowError(showError);
    }

    public InitializeGuiException(boolean showError) {
		this.setShowError(showError);
    }

    public InitializeGuiException(String message) {
        super(message);
    }

    public InitializeGuiException(String message, Throwable cause) {
        super(message, cause);
    }

    public InitializeGuiException(Throwable cause) {
        super(cause);
    }

    public InitializeGuiException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public String getMessageDefault() {
    	return I18N.getText("Error inesperado. Para mas información consulte el log.");
    }

	public boolean isShowError() {
		return showError;
	}

	public void setShowError(boolean showError) {
		this.showError = showError;
	}
    
}
