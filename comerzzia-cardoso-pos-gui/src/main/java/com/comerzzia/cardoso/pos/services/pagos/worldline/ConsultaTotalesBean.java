package com.comerzzia.cardoso.pos.services.pagos.worldline;

import java.math.BigDecimal;

public class ConsultaTotalesBean {

	private BigDecimal totalVentas;
	private Integer numVentas;
	private BigDecimal totalDevoluciones;
	private Integer numDevoluciones;
	private BigDecimal totalAnulaciones;
	private Integer numAnulaciones;

	public ConsultaTotalesBean() {
		totalVentas = BigDecimal.ZERO;
		numVentas = 0;
		totalDevoluciones = BigDecimal.ZERO;
		numDevoluciones = 0;
		totalAnulaciones = BigDecimal.ZERO;
		numAnulaciones = 0;
	}

	public BigDecimal getTotalVentas() {
		return totalVentas;
	}

	public void setTotalVentas(BigDecimal totalVentas) {
		this.totalVentas = totalVentas;
	}

	public Integer getNumVentas() {
		return numVentas;
	}

	public void setNumVentas(Integer numVentas) {
		this.numVentas = numVentas;
	}

	public BigDecimal getTotalDevoluciones() {
		return totalDevoluciones;
	}

	public void setTotalDevoluciones(BigDecimal totalDevoluciones) {
		this.totalDevoluciones = totalDevoluciones;
	}

	public Integer getNumDevoluciones() {
		return numDevoluciones;
	}

	public void setNumDevoluciones(Integer numDevoluciones) {
		this.numDevoluciones = numDevoluciones;
	}

	public BigDecimal getTotalAnulaciones() {
		return totalAnulaciones;
	}

	public void setTotalAnulaciones(BigDecimal totalAnulaciones) {
		this.totalAnulaciones = totalAnulaciones;
	}

	public Integer getNumAnulaciones() {
		return numAnulaciones;
	}

	public void setNumAnulaciones(Integer numAnulaciones) {
		this.numAnulaciones = numAnulaciones;
	}

}
