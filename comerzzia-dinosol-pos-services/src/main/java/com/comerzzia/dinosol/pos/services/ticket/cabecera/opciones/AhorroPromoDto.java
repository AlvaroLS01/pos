package com.comerzzia.dinosol.pos.services.ticket.cabecera.opciones;

import java.math.BigDecimal;

public class AhorroPromoDto {

	private Long idPromocion;

	private BigDecimal ahorro;

	private Long tipoAhorro;

	public AhorroPromoDto() {
		super();
	}

	public AhorroPromoDto(Long idPromocion, BigDecimal ahorro, Long tipoDescuento) {
		super();
		this.idPromocion = idPromocion;
		this.ahorro = ahorro;
		this.tipoAhorro = tipoDescuento;
	}

	public Long getIdPromocion() {
		return idPromocion;
	}

	public void setIdPromocion(Long idPromocion) {
		this.idPromocion = idPromocion;
	}

	public BigDecimal getAhorro() {
		return ahorro;
	}

	public void setAhorro(BigDecimal ahorro) {
		this.ahorro = ahorro;
	}

	public Long getTipoAhorro() {
		return tipoAhorro;
	}

	public void setTipoAhorro(Long tipoAhorro) {
		this.tipoAhorro = tipoAhorro;
	}

}
