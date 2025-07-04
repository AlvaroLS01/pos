package com.comerzzia.cardoso.pos.persistence.articulos.buscar;

import java.math.BigDecimal;

import com.comerzzia.pos.persistence.articulos.buscar.ArticuloBuscarBean;

/**
 * GAP - DESCUENTO TARIFA
 */
public class CardosoArticuloBuscarBean extends ArticuloBuscarBean {

	private Boolean promocionado;
	private BigDecimal descuentoTarifa;

	public Boolean getPromocionado() {
		return promocionado;
	}

	public void setPromocionado(Boolean promocionado) {
		this.promocionado = promocionado;
	}

	public BigDecimal getDescuentoTarifa() {
		return descuentoTarifa;
	}

	public void setDescuentoTarifa(BigDecimal descuentoTarifa) {
		this.descuentoTarifa = descuentoTarifa;
	}

}
