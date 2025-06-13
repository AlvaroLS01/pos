package com.comerzzia.bimbaylola.pos.services.core.config.configContadores.rangos;

import com.comerzzia.core.util.base.Exception;

public class ConfigContadoresRangosException extends Exception {

	private static final long serialVersionUID = 2737149980340304069L;

	public ConfigContadoresRangosException() {
	}

	public ConfigContadoresRangosException(String msg) {
		super(msg);
	}

	public ConfigContadoresRangosException(String msg, Throwable e) {
		super(msg, e);
	}

	public ConfigContadoresRangosException(String msg, String msgKey) {
		super(msg, msgKey);
	}

	public ConfigContadoresRangosException(String msg, String msgKey, Throwable e) {
		super(msg, msgKey, e);
	}

}
