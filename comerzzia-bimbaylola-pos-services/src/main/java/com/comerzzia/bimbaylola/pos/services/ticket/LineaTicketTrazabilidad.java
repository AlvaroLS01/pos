package com.comerzzia.bimbaylola.pos.services.ticket;

import java.util.List;

public class LineaTicketTrazabilidad {

	protected Boolean tieneTrazabilidad;
	protected List<String> cadenasTrazabilidad;
	protected String status;

	public Boolean getTieneTrazabilidad() {
		return tieneTrazabilidad;
	}

	public void setTieneTrazabilidad(Boolean tieneTrazabilidad) {
		this.tieneTrazabilidad = tieneTrazabilidad;
	}

	public List<String> getCadenasTrazabilidad() {
		return cadenasTrazabilidad;
	}

	public void setCadenasTrazabilidad(List<String> cadenasTrazabilidad) {
		this.cadenasTrazabilidad = cadenasTrazabilidad;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
