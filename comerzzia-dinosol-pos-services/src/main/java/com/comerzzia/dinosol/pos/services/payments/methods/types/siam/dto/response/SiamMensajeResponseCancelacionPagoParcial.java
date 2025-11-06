package com.comerzzia.dinosol.pos.services.payments.methods.types.siam.dto.response;


public class SiamMensajeResponseCancelacionPagoParcial {
	
	protected String codigoRespuesta;
	protected String centroAutorizador;
	protected String secuencia;
	
	public String getCodigoRespuesta() {
		return codigoRespuesta;
	}

	public void setCodigoRespuesta(String codigoRespuesta) {
		this.codigoRespuesta = codigoRespuesta;
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

}
