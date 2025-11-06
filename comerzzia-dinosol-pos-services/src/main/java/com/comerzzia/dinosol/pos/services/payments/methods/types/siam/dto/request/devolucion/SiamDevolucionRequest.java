package com.comerzzia.dinosol.pos.services.payments.methods.types.siam.dto.request.devolucion;

public class SiamDevolucionRequest {

	protected String estado;
	protected SiamDevolucionMensajeRequest mensaje;

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public SiamDevolucionMensajeRequest getMensaje() {
		return mensaje;
	}

	public void setMensaje(SiamDevolucionMensajeRequest mensaje) {
		this.mensaje = mensaje;
	}

	@Override
	public String toString() {
		return estado + mensaje.toString();
	}
}
