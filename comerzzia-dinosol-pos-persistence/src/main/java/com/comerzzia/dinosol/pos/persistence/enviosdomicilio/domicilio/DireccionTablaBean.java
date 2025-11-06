package com.comerzzia.dinosol.pos.persistence.enviosdomicilio.domicilio;

import com.comerzzia.dinosol.librerias.sherpa.client.domicilio.model.DomicilioResponse;
import com.comerzzia.dinosol.librerias.sherpa.client.domicilio.model.DomicilioResponseCrud;

public class DireccionTablaBean {

	private String contenidoTabla;
	private DireccionDatosBean direccionDatos;
	private DomicilioResponse direcciones;
	private DomicilioResponseCrud direccionSeleccionada;
	private String estado;
	private String sherpaCodeDireccion;
	
	public String getContenidoTabla() {
		return contenidoTabla;
	}

	public void setContenidoTabla(String contenidoTabla) {
		this.contenidoTabla = contenidoTabla;
	}

	public DireccionDatosBean getDireccionDatos() {
		return direccionDatos;
	}

	public void setDireccionDatos(DireccionDatosBean direccionDatos) {
		this.direccionDatos = direccionDatos;
	}

	public DomicilioResponse getDirecciones() {
		return direcciones;
	}

	public void setDirecciones(DomicilioResponse direcciones) {
		this.direcciones = direcciones;
	}

	public DomicilioResponseCrud getDireccionSeleccionada() {
		return direccionSeleccionada;
	}

	public void setDireccionSeleccionada(DomicilioResponseCrud direccionSeleccionada) {
		this.direccionSeleccionada = direccionSeleccionada;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getSherpaCodeDireccion() {
		return sherpaCodeDireccion;
	}

	public void setSherpaCodeDireccion(String sherpaCodeDireccion) {
		this.sherpaCodeDireccion = sherpaCodeDireccion;
	}
	
}
