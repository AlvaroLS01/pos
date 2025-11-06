package com.comerzzia.dinosol.pos.gui.tarjetas.saldo;

import java.math.BigDecimal;

public class ConsultaSaldoDto {

	private BigDecimal saldo;

	private String estado;

	private Object datosAnexos;

	public ConsultaSaldoDto(BigDecimal saldo, String estado) {
		super();
		this.saldo = saldo;
		this.estado = estado;
	}

	public BigDecimal getSaldo() {
		return saldo;
	}

	public void setSaldo(BigDecimal saldo) {
		this.saldo = saldo;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Object getDatosAnexos() {
		return datosAnexos;
	}

	public void setDatosAnexos(Object datosAnexos) {
		this.datosAnexos = datosAnexos;
	}

}
