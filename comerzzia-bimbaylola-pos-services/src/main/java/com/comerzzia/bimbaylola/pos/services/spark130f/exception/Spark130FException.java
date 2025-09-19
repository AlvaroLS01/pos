package com.comerzzia.bimbaylola.pos.services.spark130f.exception;

import com.comerzzia.core.util.base.Exception;

public class Spark130FException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Spark130FException() {
		super();
	}

	public Spark130FException(String msg) {
		super(msg);
	}

	public Spark130FException(String msg, Throwable e) {
		super(msg, e);
	}

	public Spark130FException(String msg, String msgKey, Throwable e) {
		super(msg, msgKey, e);
	}

	public Spark130FException(String msg, String msgKey) {
		super(msg, msgKey);
	}

}
