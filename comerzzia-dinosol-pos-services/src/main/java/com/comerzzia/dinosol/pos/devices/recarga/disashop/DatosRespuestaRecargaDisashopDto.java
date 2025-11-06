package com.comerzzia.dinosol.pos.devices.recarga.disashop;

import com.comerzzia.dinosol.librerias.disashop.client.recarga.RespuestaRecargaDisashop;
import com.comerzzia.dinosol.pos.devices.recarga.dto.recarga.DatosRespuestaRecargaDto;

public class DatosRespuestaRecargaDisashopDto extends DatosRespuestaRecargaDto {

	private String error;

	private String mensaje;

	private String referenciaProveedor;

	public DatosRespuestaRecargaDisashopDto(RespuestaRecargaDisashop response) {
		this.error = response.getError();
		this.mensaje = response.getMensaje();
		this.referenciaProveedor = response.getReferenciaProveedor();
		setPin(response.getPin());
	}

	@Override
	public boolean isRespuestaOk() {
		return "0".equals(error);
	}

	@Override
	public String getMensaje() {
		return mensaje;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getReferenciaProveedor() {
		return referenciaProveedor;
	}

	public void setReferenciaProveedor(String referenciaProveedor) {
		this.referenciaProveedor = referenciaProveedor;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

}
