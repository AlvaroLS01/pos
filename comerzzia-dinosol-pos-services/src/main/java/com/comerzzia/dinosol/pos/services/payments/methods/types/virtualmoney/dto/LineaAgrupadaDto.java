package com.comerzzia.dinosol.pos.services.payments.methods.types.virtualmoney.dto;

import java.math.BigDecimal;

public class LineaAgrupadaDto {

	private String codart;

	private BigDecimal precio, cantidad;

	public String getCodart() {
		return codart;
	}

	public void setCodart(String codart) {
		this.codart = codart;
	}

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
	
	public void addCantidad(BigDecimal cantidad) {
		if(this.cantidad == null) {
			this.cantidad = BigDecimal.ZERO;
		}
		this.cantidad = this.cantidad.add(cantidad);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LineaAgrupadaDto other = (LineaAgrupadaDto) obj;
		if (cantidad == null) {
			if (other.cantidad != null)
				return false;
		}
		else if (!cantidad.equals(other.cantidad))
			return false;
		if (codart == null) {
			if (other.codart != null)
				return false;
		}
		else if (!codart.equals(other.codart))
			return false;
		return true;
	}

}
