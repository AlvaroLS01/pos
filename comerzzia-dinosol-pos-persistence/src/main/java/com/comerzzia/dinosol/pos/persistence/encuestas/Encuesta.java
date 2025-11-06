package com.comerzzia.dinosol.pos.persistence.encuestas;

import java.util.Date;
import java.util.List;

import com.comerzzia.dinosol.pos.persistence.encuestas.preguntas.PreguntaEncuesta;

public class Encuesta extends EncuestaKey {

	private String descripcion;

	private Date fechaInicio;

	private Date fechaFin;

	private String mostrarEnVisor;

	private String activo;

	List<PreguntaEncuesta> preguntas;

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion == null ? null : descripcion.trim();
	}

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public Date getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

	public String getMostrarEnVisor() {
		return mostrarEnVisor;
	}

	public void setMostrarEnVisor(String mostrarEnVisor) {
		this.mostrarEnVisor = mostrarEnVisor == null ? null : mostrarEnVisor.trim();
	}

	public String getActivo() {
		return activo;
	}

	public void setActivo(String activo) {
		this.activo = activo;
	}

	public List<PreguntaEncuesta> getPreguntas() {
		return preguntas;
	}

	public void setPreguntas(List<PreguntaEncuesta> preguntas) {
		this.preguntas = preguntas;
	}

}