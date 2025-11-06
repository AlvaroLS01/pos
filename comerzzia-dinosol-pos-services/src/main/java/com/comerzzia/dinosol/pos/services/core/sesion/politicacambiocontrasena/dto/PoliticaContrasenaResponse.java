package com.comerzzia.dinosol.pos.services.core.sesion.politicacambiocontrasena.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "politica_contrasena_response")
@XmlAccessorType(XmlAccessType.FIELD)
public class PoliticaContrasenaResponse {

	@XmlElement(name = "usuario")
	private PoliticaContrasenaUsuario usuario;

	@XmlElement(name = "usuario_politica_cambio")
	private PoliticaContrasena usuarioPoliticaCambio;

	@XmlElement(name = "login_correcto")
	private boolean loginCorrecto;

	@XmlElement(name = "proxima_caducidad")
	private boolean proximaCaducidad;

	@XmlElement(name = "codError")
	private Integer codError;

	@XmlElement(name = "mensaje")
	private String mensaje;

	@XmlElement(name = "numeroUpdates")
	private Integer numeroUpdates = 0;

	public PoliticaContrasenaUsuario getUsuario() {
		return usuario;
	}

	public void setUsuario(PoliticaContrasenaUsuario usuario) {
		this.usuario = usuario;
	}

	public PoliticaContrasena getUsuarioPoliticaCambio() {
		return usuarioPoliticaCambio;
	}

	public void setUsuarioPoliticaCambio(PoliticaContrasena usuarioPoliticaCambio) {
		this.usuarioPoliticaCambio = usuarioPoliticaCambio;
	}

	public boolean isLoginCorrecto() {
		return loginCorrecto;
	}

	public void setLoginCorrecto(boolean loginCorrecto) {
		this.loginCorrecto = loginCorrecto;
	}

	public boolean isProximaCaducidad() {
		return proximaCaducidad;
	}

	public void setProximaCaducidad(boolean proximaCaducidad) {
		this.proximaCaducidad = proximaCaducidad;
	}

	public Integer getCodError() {
		return codError;
	}

	public void setCodError(Integer codError) {
		this.codError = codError;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public Integer getNumeroUpdates() {
		return numeroUpdates;
	}

	public void setNumeroUpdates(Integer numeroUpdates) {
		this.numeroUpdates = numeroUpdates;
	}

}
