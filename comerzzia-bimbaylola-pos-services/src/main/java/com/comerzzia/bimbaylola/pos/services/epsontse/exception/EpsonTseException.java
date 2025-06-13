package com.comerzzia.bimbaylola.pos.services.epsontse.exception;

import com.comerzzia.core.util.base.Exception;

@SuppressWarnings("serial")
public class EpsonTseException extends Exception {

	public EpsonTseException() {
		super();
	}

	public EpsonTseException(String msg) {
		super(msg);
	}

	public EpsonTseException(String msg, Throwable e) {
		super(msg, e);
	}

	public EpsonTseException(String msg, String msgKey, Throwable e) {
		super(msg, msgKey, e);
	}

	public EpsonTseException(String msg, String msgKey) {
		super(msg, msgKey);
	}

}
