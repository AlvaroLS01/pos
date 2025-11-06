package com.comerzzia.dinosol.pos.services.core.sesion.politicacambiocontrasena.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "politica_contrasena_usuario")
@XmlAccessorType(XmlAccessType.FIELD)
public class PoliticaContrasenaUsuario {

	@XmlElement(name = "uidInstancia")
	private String uidInstancia;

	@XmlElement(name = "idUsuario")
	private Long idUsuario;

	@XmlElement(name = "usuario")
	private String usuario;

	@XmlElement(name = "desUsuario")
	private String desUsuario;

	@XmlElement(name = "clave")
	private String clave;

	public String getUidInstancia() {
		return uidInstancia;
	}

	public void setUidInstancia(String uidInstancia) {
		this.uidInstancia = uidInstancia;
	}

	public Long getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(Long idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getDesUsuario() {
		return desUsuario;
	}

	public void setDesUsuario(String desUsuario) {
		this.desUsuario = desUsuario;
	}

	public String getClave() {
		return clave;
	}

	public void setClave(String clave) {
		this.clave = clave;
	}

}
