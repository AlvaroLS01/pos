package com.comerzzia.dinosol.pos.gui.ventas.tickets.articulos.anulacion;

import java.math.BigDecimal;

public class AnulacionArticuloDto {

	private BigDecimal precio;

	private BigDecimal cantidad;
	
	private Boolean precioModificadoEnOrigen;

	public BigDecimal getPrecio() {
		return precio;
	}

	public void setPrecio(BigDecimal precio) {
		this.precio = precio;
	}

	public BigDecimal getCantidad() {
		return cantidad;
	}

	public void setCantidad(BigDecimal cantidad) {
		this.cantidad = cantidad;
	}

	public Boolean getPrecioModificadoEnOrigen() {
		return precioModificadoEnOrigen;
	}

	public void setPrecioModificadoEnOrigen(Boolean precioModificadoEnOrigen) {
		this.precioModificadoEnOrigen = precioModificadoEnOrigen;
	}

	
}
