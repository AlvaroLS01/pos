package com.comerzzia.dinosol.pos.services.payments.methods.types.siam.dto.request.cancelarpago;


public class SiamCancelarPagoRequest {

	protected String estado;

	protected SiamCancelarPagoMensajeRequest mensaje;

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public SiamCancelarPagoMensajeRequest getMensaje() {
		return mensaje;
	}

	public void setMensaje(SiamCancelarPagoMensajeRequest mensaje) {
		this.mensaje = mensaje;
	}

	@Override
	public String toString() {
		return estado + mensaje.toString();
	}

}
