package com.comerzzia.cardoso.pos.persistence.tarifas;

import java.math.BigDecimal;

/**
 * GAP - DESCUENTO TARIFA
 */
public class TarifaDetalleAnexaBean extends TarifaDetalleAnexaKey{

	private BigDecimal precioVentaSinDto;

	private BigDecimal precioTotalSinDto;

	private BigDecimal descuentoTarifa;

	public BigDecimal getPrecioVentaSinDto(){
		return precioVentaSinDto;
	}

	public void setPrecioVentaSinDto(BigDecimal precioVentaSinDto){
		this.precioVentaSinDto = precioVentaSinDto;
	}

	public BigDecimal getPrecioTotalSinDto(){
		return precioTotalSinDto;
	}

	public void setPrecioTotalSinDto(BigDecimal precioTotalSinDto){
		this.precioTotalSinDto = precioTotalSinDto;
	}

	public BigDecimal getDescuentoTarifa(){
		return descuentoTarifa;
	}

	public void setDescuentoTarifa(BigDecimal descuentoTarifa){
		this.descuentoTarifa = descuentoTarifa;
	}

}