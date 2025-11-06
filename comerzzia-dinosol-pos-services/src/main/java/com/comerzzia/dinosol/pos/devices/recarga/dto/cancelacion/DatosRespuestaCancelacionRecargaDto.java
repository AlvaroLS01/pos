package com.comerzzia.dinosol.pos.devices.recarga.dto.cancelacion;

public abstract class DatosRespuestaCancelacionRecargaDto {

	protected String referenciaProveedor;

	public abstract boolean isRespuestaOk();

	public abstract String getMensaje();

	public String getReferenciaProveedor() {
		return referenciaProveedor;
	}

	public void setReferenciaProveedor(String referenciaProveedor) {
		this.referenciaProveedor = referenciaProveedor;
	}

}
