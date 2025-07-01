package com.comerzzia.pos.util.mybatis.exception;

public class KeyConstraintViolationException extends PersistenceException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 7193157860994198356L;

	public KeyConstraintViolationException() {
		super();
	}

	public KeyConstraintViolationException(String message, Throwable cause) {
		super(message, cause);
	}

	public KeyConstraintViolationException(String message) {
		super(message);
	}

	public KeyConstraintViolationException(Throwable cause) {
		super(cause);
	}

	public String getDefaultMessage() {
		return "A record with the same code already exists in the system";
	}

	@Override
	public boolean isKeyConstraintViolationException() {
		return true;
	}
}
