package com.comerzzia.dinosol.pos.devices.recarga.dto.recarga;

public abstract class DatosRespuestaRecargaDto {

	private String telefono;

	private String referenciaProveedor;

	private String pin;

	public abstract boolean isRespuestaOk();

	public abstract String getMensaje();

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getReferenciaProveedor() {
		return referenciaProveedor;
	}

	public void setReferenciaProveedor(String referenciaProveedor) {
		this.referenciaProveedor = referenciaProveedor;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

}
