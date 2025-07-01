package com.comerzzia.pos.util.mybatis.exception;

public class ConstraintViolationException extends PersistenceException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -6143560829444624288L;

	public ConstraintViolationException() {
	}

	public ConstraintViolationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConstraintViolationException(String message) {
		super(message);
	}

	public ConstraintViolationException(Throwable cause) {
		super(cause);
	}

	public String getDefaultMessage() {
		return "Integrity constraints between records have been violated";
	}

	@Override
	public boolean isConstraintViolationException() {
		return true;
	}

}
