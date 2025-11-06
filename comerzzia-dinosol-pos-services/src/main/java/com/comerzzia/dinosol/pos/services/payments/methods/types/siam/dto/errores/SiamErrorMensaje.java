package com.comerzzia.dinosol.pos.services.payments.methods.types.siam.dto.errores;

public class SiamErrorMensaje {

	protected String codigoRespuesta;
	protected String centroAutorizador;
	protected String codigoError;
	protected String mensajeError00;
	
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
	public String getCodigoError() {
		return codigoError;
	}
	public void setCodigoError(String codigoError) {
		this.codigoError = codigoError;
	}
	
	public String getMensajeError00() {
		return mensajeError00;
	}
	
	public void setMensajeError00(String mensajeError00) {
		this.mensajeError00 = mensajeError00;
	}
	
}
