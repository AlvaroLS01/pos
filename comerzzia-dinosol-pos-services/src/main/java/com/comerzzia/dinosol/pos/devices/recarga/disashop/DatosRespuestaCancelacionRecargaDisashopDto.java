package com.comerzzia.dinosol.pos.devices.recarga.disashop;

import com.comerzzia.dinosol.librerias.disashop.client.devolucion.RespuestaDevolucionDisashop;
import com.comerzzia.dinosol.pos.devices.recarga.dto.cancelacion.DatosRespuestaCancelacionRecargaDto;

public class DatosRespuestaCancelacionRecargaDisashopDto extends DatosRespuestaCancelacionRecargaDto {
	
	private String error;
	
	private String mensaje;

	public DatosRespuestaCancelacionRecargaDisashopDto(RespuestaDevolucionDisashop response) {
		this.error = response.getError();
		this.mensaje = response.getMensaje();
		this.referenciaProveedor = response.getReferenciaProveedor();
	}

	@Override
	public boolean isRespuestaOk() {
		return "0".equals(error);
	}

	@Override
	public String getMensaje() {
		return mensaje;
	}

}
