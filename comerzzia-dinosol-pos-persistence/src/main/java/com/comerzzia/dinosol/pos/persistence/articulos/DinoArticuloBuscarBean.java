package com.comerzzia.dinosol.pos.persistence.articulos;

import com.comerzzia.pos.persistence.articulos.buscar.ArticuloBuscarBean;

public class DinoArticuloBuscarBean extends ArticuloBuscarBean {

	private String eanPrincipal;

	public String getEanPrincipal() {
		return eanPrincipal;
	}

	public void setEanPrincipal(String eanPrincipal) {
		this.eanPrincipal = eanPrincipal;
	}

}
