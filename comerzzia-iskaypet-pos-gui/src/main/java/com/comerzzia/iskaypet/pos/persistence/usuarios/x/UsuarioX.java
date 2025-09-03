package com.comerzzia.iskaypet.pos.persistence.usuarios.x;

import java.util.Date;

public class UsuarioX extends UsuarioXKey {
    private Date fechaPassword;

	public Date getFechaPassword() {
		return fechaPassword;
	}

	public void setFechaPassword(Date fechaPassword) {
		this.fechaPassword = fechaPassword;
	}

}