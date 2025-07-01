package com.comerzzia.pos.util.mybatis.exception;

public class SQLTimeoutException extends PersistenceException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 7193157860994198356L;

	public SQLTimeoutException() {
		super();
	}

	public SQLTimeoutException(String message, Throwable cause) {
		super(message, cause);
	}

	public SQLTimeoutException(String message) {
		super(message);
	}

	public SQLTimeoutException(Throwable cause) {
		super(cause);
	}

	public String getDefaultMessage() {
		return "Database record lock timed out";
	}

	@Override
	public boolean isSQLTimeoutException() {
		return true;
	}
}
