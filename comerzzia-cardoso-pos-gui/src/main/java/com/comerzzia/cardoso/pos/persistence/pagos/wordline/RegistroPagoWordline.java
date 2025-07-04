package com.comerzzia.cardoso.pos.persistence.pagos.wordline;

import java.math.BigDecimal;

public class RegistroPagoWordline extends RegistroPagoWordlineKey {

	private BigDecimal importe;

	private Integer numOperVenta;

	private Integer numOperDevol;

	private Integer numOperAnul;

	public RegistroPagoWordline() {
		numOperVenta = 0;
		numOperDevol = 0;
		numOperAnul = 0;
	}

	public BigDecimal getImporte() {
		return importe;
	}

	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}

	public Integer getNumOperVenta() {
		return numOperVenta;
	}

	public void setNumOperVenta(Integer numOperVenta) {
		this.numOperVenta = numOperVenta;
	}

	public Integer getNumOperDevol() {
		return numOperDevol;
	}

	public void setNumOperDevol(Integer numOperDevol) {
		this.numOperDevol = numOperDevol;
	}

	public Integer getNumOperAnul() {
		return numOperAnul;
	}

	public void setNumOperAnul(Integer numOperAnul) {
		this.numOperAnul = numOperAnul;
	}
}