package com.comerzzia.bimbaylola.pos.services.dispositivofirma;

import com.comerzzia.pos.util.i18n.I18N;

public class DispositivoFirmaException extends Exception {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	public DispositivoFirmaException() {
	}

	public DispositivoFirmaException(String message) {
		super(message);
	}

	public DispositivoFirmaException(String message, Throwable cause) {
		super(message, cause);
	}

	public DispositivoFirmaException(Throwable cause) {
		super(cause);
	}

	public String getMessageDefault() {
		return I18N.getTexto("Error en el proceso de firma.");
	}

}
