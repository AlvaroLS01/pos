package com.comerzzia.dinosol.pos.devices.recarga.disashop;

import com.comerzzia.dinosol.pos.devices.recarga.dto.DatosConexionServicioRecargaDto;

public class DatosConexionDisashopDto extends DatosConexionServicioRecargaDto {

	private String entidad;

	private String idTerminal;

	private String idCliente;

	public String getEntidad() {
		return entidad;
	}

	public void setEntidad(String entidad) {
		this.entidad = entidad;
	}

	public String getIdTerminal() {
		return idTerminal;
	}

	public void setIdTerminal(String idTerminal) {
		this.idTerminal = idTerminal;
	}

	public String getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(String idCliente) {
		this.idCliente = idCliente;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append("url=").append(getUrl());
		sb.append(", usuario=").append(getUsuario());
		sb.append(", password=").append(getPassword());
		sb.append(", entidad=").append(entidad);
		sb.append(", idTerminal=").append(idTerminal);
		sb.append(", idCliente=").append(idCliente);
		sb.append("]");
		return sb.toString();
	}

}
