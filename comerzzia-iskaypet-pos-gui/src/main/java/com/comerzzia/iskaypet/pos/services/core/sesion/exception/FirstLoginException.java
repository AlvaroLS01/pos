package com.comerzzia.iskaypet.pos.services.core.sesion.exception;

import com.comerzzia.pos.services.core.usuarios.UsuarioInvalidLoginException;
import com.comerzzia.pos.util.i18n.I18N;

public class FirstLoginException extends UsuarioInvalidLoginException {

	/**
	* 
	*/
	private static final long serialVersionUID = 6484380833087288688L;

	public FirstLoginException() {
	}

	public FirstLoginException(String message) {
		super(message);
	}

	public FirstLoginException(String message, Throwable cause) {
		super(message, cause);
	}

	public FirstLoginException(Throwable cause) {
		super(cause);
	}

	public FirstLoginException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	@Override
	public String getMessageDefault() {
		return I18N.getTexto("El primer inicio de sesión requiere una renovación de la contraseña");
	}

}
