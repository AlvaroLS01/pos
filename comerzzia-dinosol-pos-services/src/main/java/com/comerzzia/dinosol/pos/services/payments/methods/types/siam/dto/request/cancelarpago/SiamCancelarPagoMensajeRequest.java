package com.comerzzia.dinosol.pos.services.payments.methods.types.siam.dto.request.cancelarpago;

public class SiamCancelarPagoMensajeRequest {

	protected String transaccion;

	protected String centroAutorizador;

	protected String secuencia;

	public String getTransaccion() {
		return transaccion;
	}

	public void setTransaccion(String transaccion) {
		this.transaccion = transaccion;
	}

	public String getCentroAutorizador() {
		return centroAutorizador;
	}

	public void setCentroAutorizador(String centroAutorizador) {
		this.centroAutorizador = centroAutorizador;
	}

	public String getSecuencia() {
		return secuencia;
	}

	public void setSecuencia(String secuencia) {
		this.secuencia = secuencia;
	}

	@Override
	public String toString() {
		return transaccion + centroAutorizador + secuencia;
	}

}
