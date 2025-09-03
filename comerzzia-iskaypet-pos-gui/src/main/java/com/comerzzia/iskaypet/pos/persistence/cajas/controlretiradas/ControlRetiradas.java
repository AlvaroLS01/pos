package com.comerzzia.iskaypet.pos.persistence.cajas.controlretiradas;

import java.math.BigDecimal;

public class ControlRetiradas {
	private String uidActividad;
	private String uidDiarioCaja;
	private BigDecimal saldoEfectivo;

	public ControlRetiradas() {
	}

	public String getUidActividad() {
		return uidActividad;
	}

	public void setUidActividad(String uidActividad) {
		this.uidActividad = uidActividad;
	}

	public String getUidDiarioCaja() {
		return uidDiarioCaja;
	}

	public void setUidDiarioCaja(String uidDiarioCaja) {
		this.uidDiarioCaja = uidDiarioCaja;
	}

	public BigDecimal getSaldoEfectivo() {
		return saldoEfectivo;
	}

	public void setSaldoEfectivo(BigDecimal saldoEfectivo) {
		this.saldoEfectivo = saldoEfectivo;
	}

	@Override
	public String toString() {
		return "Dcdt{" + "uidActividad='" + uidActividad + '\'' + ", uidDiarioCaja='" + uidDiarioCaja + '\''
				+ ", saldoEfectivo=" + saldoEfectivo + '}';
	}
}
