package com.comerzzia.api.loyalty.persistence.collectivesTypes;

import com.comerzzia.core.util.base.ParametrosBuscarBean;

public class ParametrosBuscarTiposColectivosBean extends ParametrosBuscarBean {

	private static final long serialVersionUID = 3276236184383729331L;

	private String codtipcolectivo = null;
	private String destipcolectivo = null;
	private String activo = null;
	private String privado = null;

	protected static final String TRUE = "S";

	public ParametrosBuscarTiposColectivosBean() {
		super.setOrden("CODTIPCOLECTIVO");
	}

	public String getCodtipcolectivo() {
		return codtipcolectivo;
	}

	public void setCodtipcolectivo(String codtipcolectivo) {
		this.codtipcolectivo = codtipcolectivo;
	}

	public String getDestipcolectivo() {
		return destipcolectivo;
	}

	public void setDestipcolectivo(String destipcolectivo) {
		this.destipcolectivo = destipcolectivo;
	}

	public String getActivo() {
		return activo;
	}

	public void setActivo(String activo) {
		this.activo = activo;
	}

	public Boolean isActivo() {
		if (activo.equalsIgnoreCase(TRUE)) {
			return true;
		}
		return false;
	}

	public String getPrivado() {
		return privado;
	}

	public void setPrivado(String privado) {
		this.privado = privado;
	}
	
	public Boolean isPrivado() {
		if (privado.equalsIgnoreCase(TRUE)) {
			return true;
		}
		return false;
	}
}
