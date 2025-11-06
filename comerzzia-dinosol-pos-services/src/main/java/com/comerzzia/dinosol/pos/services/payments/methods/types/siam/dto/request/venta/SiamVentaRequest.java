package com.comerzzia.dinosol.pos.services.payments.methods.types.siam.dto.request.venta;

public class SiamVentaRequest {
	
	protected String estado;
	
	protected SiamVentaMensajeRequest mensaje;

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public SiamVentaMensajeRequest getMensaje() {
		return mensaje;
	}

	public void setMensaje(SiamVentaMensajeRequest mensaje) {
		this.mensaje = mensaje;
	}

	@Override
	public String toString() {
		return estado + mensaje.toString();
	}

}
