package com.comerzzia.dinosol.pos.persistence.posreparto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "aplicaciones_reparto")
public class POSAppRepartoDto {

	private String codTar;

	public String getCodTar() {
		return codTar;
	}

	public void setCodTar(String codTar) {
		this.codTar = codTar;
	}
}
