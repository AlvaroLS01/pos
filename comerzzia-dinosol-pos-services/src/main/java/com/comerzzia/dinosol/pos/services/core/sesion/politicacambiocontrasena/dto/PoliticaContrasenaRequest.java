package com.comerzzia.dinosol.pos.services.core.sesion.politicacambiocontrasena.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "politica_contrasena")
@XmlAccessorType(XmlAccessType.FIELD)
public class PoliticaContrasenaRequest {

	@XmlElement(name = "usuario")
	private PoliticaContrasenaUsuario usuario;

	@XmlElement(name = "tiene_politica_cambio")
	private Boolean tienePoliticaCambio;

	public PoliticaContrasenaUsuario getUsuario() {
		return usuario;
	}

	public void setUsuario(PoliticaContrasenaUsuario usuario) {
		this.usuario = usuario;
	}

	public Boolean getTienePoliticaCambio() {
		return tienePoliticaCambio;
	}

	public void setTienePoliticaCambio(Boolean tienePoliticaCambio) {
		this.tienePoliticaCambio = tienePoliticaCambio;
	}

}
