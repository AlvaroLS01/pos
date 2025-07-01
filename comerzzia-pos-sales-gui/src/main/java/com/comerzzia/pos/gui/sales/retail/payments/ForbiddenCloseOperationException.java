package com.comerzzia.pos.gui.sales.retail.payments;

import com.comerzzia.core.commons.exception.BusinessException;

public class ForbiddenCloseOperationException extends BusinessException {

	private static final long serialVersionUID = 8544955571810957865L;

	public ForbiddenCloseOperationException() {
    }

    public ForbiddenCloseOperationException(String message) {
        super(message);
    }

    public ForbiddenCloseOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ForbiddenCloseOperationException(Throwable cause) {
        super(cause);
    }

    public ForbiddenCloseOperationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

	@Override
    public String getMessageDefault() {
	    return null;
    }

}
