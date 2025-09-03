package com.comerzzia.iskaypet.pos.api.rest.client.fidelizados.response;

import com.comerzzia.api.rest.client.fidelizados.ResponseGetFidelizadoRest;


public class IskaypetResponseGetFidelizadoRest extends ResponseGetFidelizadoRest {

	private Double saldoNoDisponible;

	public IskaypetResponseGetFidelizadoRest() {
	}


	public Double getSaldoNoDisponible() {
		return saldoNoDisponible;
	}

	public void setSaldoNoDisponible(Double saldoNoDisponible) {
		this.saldoNoDisponible = saldoNoDisponible;
	}
}
