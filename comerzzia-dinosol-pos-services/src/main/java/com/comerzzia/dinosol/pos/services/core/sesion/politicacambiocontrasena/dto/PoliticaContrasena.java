package com.comerzzia.dinosol.pos.services.core.sesion.politicacambiocontrasena.dto;

import java.util.Date;

public class PoliticaContrasena {

	private String uidInstancia;

	private Long idUsuario;

	private Date fechaCaducidad;

	private String estado;

	private Short numIntentos;

	private String activo;

	private String contrasenaAnterior1;

	private String contrasenaAnterior2;

	private String contrasenaAnterior3;

	public Date getFechaCaducidad() {
		return fechaCaducidad;
	}

	public void setFechaCaducidad(Date fechaCaducidad) {
		this.fechaCaducidad = fechaCaducidad;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Short getNumIntentos() {
		return numIntentos;
	}

	public void setNumIntentos(Short numIntentos) {
		this.numIntentos = numIntentos;
	}

	public String getActivo() {
		return activo;
	}

	public void setActivo(String activo) {
		this.activo = activo;
	}

	public String getContrasenaAnterior1() {
		return contrasenaAnterior1;
	}

	public void setContrasenaAnterior1(String contrasenaAnterior1) {
		this.contrasenaAnterior1 = contrasenaAnterior1;
	}

	public String getContrasenaAnterior2() {
		return contrasenaAnterior2;
	}

	public void setContrasenaAnterior2(String contrasenaAnterior2) {
		this.contrasenaAnterior2 = contrasenaAnterior2;
	}

	public String getContrasenaAnterior3() {
		return contrasenaAnterior3;
	}

	public void setContrasenaAnterior3(String contrasenaAnterior3) {
		this.contrasenaAnterior3 = contrasenaAnterior3;
	}

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

}