package com.comerzzia.cardoso.pos.services.rest.client.fidelizados;

import java.math.BigDecimal;

import com.comerzzia.api.rest.client.fidelizados.ResponseGetFidelizadoRest;

public class CardosoResponseGetFidelizadoRest extends ResponseGetFidelizadoRest {

	private BigDecimal importeDescuentoAcumulado;

	public CardosoResponseGetFidelizadoRest() {
		super();
	}

	public BigDecimal getImporteDescuentoAcumulado() {
		return importeDescuentoAcumulado;
	}

	public void setImporteDescuentoAcumulado(BigDecimal importeDescuentoAcumulado) {
		this.importeDescuentoAcumulado = importeDescuentoAcumulado;
	}
	
}
