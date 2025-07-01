package com.comerzzia.pos.util.mybatis.exception;

public class NotNullConstraintViolationException extends PersistenceException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -4062105522859414976L;

	public NotNullConstraintViolationException() {
		super();
	}

	public NotNullConstraintViolationException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotNullConstraintViolationException(String message) {
		super(message);
	}

	public NotNullConstraintViolationException(Throwable cause) {
		super(cause);
	}

	public String getDefaultMessage() {
		return "A required field constraint has been violated";
	}

	@Override
	public boolean isNotNullConstraintViolationException() {
		return true;
	}
}
