package com.comerzzia.bimbaylola.pos.services.core.sesion;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.services.core.sesion.SesionUsuario;

@Component
@Primary
public class ByLSesionUsuario extends SesionUsuario {
	
	protected String password;
	
	public ByLSesionUsuario() {
		super();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
