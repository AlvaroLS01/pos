package com.comerzzia.pos.core.gui.permissions.exception;

import com.comerzzia.core.commons.exception.BusinessException;
import com.comerzzia.pos.util.i18n.I18N;

public class PermissionDeniedException extends BusinessException {

	private static final long serialVersionUID = 1842401465428940473L;

	public PermissionDeniedException() {
	}

	public PermissionDeniedException(String message) {
		super(message);
	}

	public PermissionDeniedException(String message, Throwable cause) {
		super(message, cause);
	}

	public PermissionDeniedException(Throwable cause) {
		super(cause);
	}

	public PermissionDeniedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	@Override
	public String getMessageDefault() {
		return I18N.getText("No tiene permisos para realizar la acción");
	}

}

