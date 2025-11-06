package com.comerzzia.dinosol.pos.persistence.restricciones;

import java.math.BigDecimal;

/**
 * Clase para guardar las restricciones aplicadas a las lineas de un ticket. Se carga desde un XML.
 */
public class RestriccionLineaBean {

	private String tiendas;
	private String codArt;
	private BigDecimal cantidadMaxima;
	private BigDecimal precioMaximo;
	private BigDecimal precioMinimo;

	public String getTiendas() {
		return tiendas;
	}

	public void setTiendas(String tiendas) {
		this.tiendas = tiendas;
	}

	public String getCodArt() {
		return codArt;
	}

	public void setCodArt(String codArt) {
		this.codArt = codArt;
	}

	public BigDecimal getCantidadMaxima() {
		return cantidadMaxima;
	}

	public void setCantidadMaxima(BigDecimal cantidadMaxima) {
		this.cantidadMaxima = cantidadMaxima;
	}

	public BigDecimal getPrecioMaximo() {
		return precioMaximo;
	}

	public void setPrecioMaximo(BigDecimal precioMaximo) {
		this.precioMaximo = precioMaximo;
	}

	public BigDecimal getPrecioMinimo() {
		return precioMinimo;
	}

	public void setPrecioMinimo(BigDecimal precioMinimo) {
		this.precioMinimo = precioMinimo;
	}

}
