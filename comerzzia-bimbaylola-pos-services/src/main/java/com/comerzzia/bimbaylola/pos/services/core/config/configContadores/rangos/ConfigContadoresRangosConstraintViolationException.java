package com.comerzzia.bimbaylola.pos.services.core.config.configContadores.rangos;

import com.comerzzia.core.util.base.Exception;

public class ConfigContadoresRangosConstraintViolationException extends Exception {

	private static final long serialVersionUID = -3911781738431199544L;

	public ConfigContadoresRangosConstraintViolationException() {
	}

	public ConfigContadoresRangosConstraintViolationException(String msg) {
		super(msg);
	}

	public ConfigContadoresRangosConstraintViolationException(String msg, Throwable e) {
		super(msg, e);
	}

	public ConfigContadoresRangosConstraintViolationException(String msg, String msgKey) {
		super(msg, msgKey);
	}

	public ConfigContadoresRangosConstraintViolationException(String msg, String msgKey, Throwable e) {
		super(msg, msgKey, e);
	}
}
