package com.comerzzia.api.loyalty.persistence.contactsTypes;

import com.comerzzia.core.util.base.ParametrosBuscarBean;

public class ParametrosBuscarTiposContactoBean extends ParametrosBuscarBean{

	private static final long serialVersionUID = -2757621803804597861L;
	
	private String codtipocon;
	private String destipocon;
	private String activo;
	
	public ParametrosBuscarTiposContactoBean() {
		super.setOrden("CODTIPOCON");
	}

	public String getCodtipocon() {
		return codtipocon;
	}

	public void setCodtipocon(String codtipocon) {
		this.codtipocon = codtipocon;
	}

	public String getDestipocon() {
		return destipocon;
	}

	public void setDestipocon(String destipocon) {
		this.destipocon = destipocon;
	}

	public String getActivo() {
		return activo;
	}

	public void setActivo(String activo) {
		this.activo = activo;
	}

}
