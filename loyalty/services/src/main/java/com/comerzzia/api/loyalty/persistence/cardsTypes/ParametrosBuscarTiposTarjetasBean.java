package com.comerzzia.api.loyalty.persistence.cardsTypes;

import com.comerzzia.core.util.base.ParametrosBuscarBean;

public class ParametrosBuscarTiposTarjetasBean extends ParametrosBuscarBean {

	private static final long serialVersionUID = -9044522724390495747L;
	
	private String codtipotarj = null;
	private String destipotarj = null;
	private String activo = null;

	protected static final String TRUE = "S";

	public ParametrosBuscarTiposTarjetasBean() {
		super.setOrden("CODTIPOTARJ");
	}
	
	public String getCodtipotarj() {
		return codtipotarj;
	}

	public void setCodtipotarj(String codtipotarj) {
		this.codtipotarj = codtipotarj;
	}

	public String getDestipotarj() {
		return destipotarj;
	}

	public void setDestipotarj(String destipotarj) {
		this.destipotarj = destipotarj;
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
}
