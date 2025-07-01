package com.comerzzia.pos.util.mybatis.exception;

public class ForeingKeyConstraintViolationException extends PersistenceException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -6143560829444624288L;

	public ForeingKeyConstraintViolationException() {
	}

	public ForeingKeyConstraintViolationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ForeingKeyConstraintViolationException(String message) {
		super(message);
	}

	public ForeingKeyConstraintViolationException(Throwable cause) {
		super(cause);
	}

	public String getDefaultMessage() {
		return "Integrity constraints between records have been violated";
	}

	@Override
	public boolean isForeingKeyConstraintViolationException() {
		return true;
	}

}
