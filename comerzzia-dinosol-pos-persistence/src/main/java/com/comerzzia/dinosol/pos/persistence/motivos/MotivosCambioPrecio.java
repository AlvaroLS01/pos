package com.comerzzia.dinosol.pos.persistence.motivos;

public class MotivosCambioPrecio extends MotivosCambioPrecioKey {

	private String desMotivo;

	private String visibleEnEdicion;

	public String getDesMotivo() {
		return desMotivo;
	}

	public void setDesMotivo(String desMotivo) {
		this.desMotivo = desMotivo == null ? null : desMotivo.trim();
	}

	public String getVisibleEnEdicion() {
		return visibleEnEdicion;
	}

	public void setVisibleEnEdicion(String visibleEnEdicion) {
		this.visibleEnEdicion = visibleEnEdicion == null ? null : visibleEnEdicion.trim();
	}

	// INICIO M�TODOS PERSONALIZADOS--------------------------------------------

	@Override
	public String toString() {
		return desMotivo;
	}

	// FIN M�TODOS PERSONALIZADOS-----------------------------------------------
}