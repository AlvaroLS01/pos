package com.comerzzia.dinosol.pos.devices.recarga.dto;

import org.apache.commons.lang.StringUtils;

public class DatosConexionServicioRecargaDto {

	private String url;

	private String usuario;

	private String password;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public boolean isConfigurado() {
		return StringUtils.isNotBlank(url) && StringUtils.isNotBlank(usuario) && StringUtils.isNotBlank(password);
	}

}
