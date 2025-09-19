package com.comerzzia.bimbaylola.pos.services.edicom.fieldvalues;

import java.util.Date;

import com.comerzzia.bimbaylola.pos.services.edicom.util.EdicomFormat;

public class Payfac {
	/*
	 * NO HABRÍA QUE SETEAR NADA
	 */
	
	private static final String LABEL_PAY = "PAYFAC";
	private static final String PAGO_CONTADO = "1";
	private static final String EFECTIVO = "10";
	private String terminosPago; // valor fijo
	private String numeroCuentaBancaria; // vacio
	private Date fechaVencimiento; // vacío
	private String metodoPago; // valor fijo
	
	
	public Payfac() {
		terminosPago = EFECTIVO;
		metodoPago = PAGO_CONTADO;
	}
	
	public String getTerminosPago() {
		return terminosPago;
	}
	
	public String getNumeroCuentaBancaria() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(numeroCuentaBancaria, 255);
	}
	
	public String getFechaVencimiento() {
		return EdicomFormat.devuelveFechaFormateada(fechaVencimiento);
	}
	
	public String getMetodoPago() {
		return metodoPago;
	}
	
	public void setTerminosPago(String terminosPago) {
		this.terminosPago = terminosPago;
	}
	
	public void setNumeroCuentaBancaria(String numeroCuentaBancaria) {
		this.numeroCuentaBancaria = numeroCuentaBancaria;
	}
	
	public void setFechaVencimiento(Date fechaVencimiento) {
		this.fechaVencimiento = fechaVencimiento;
	}
	
	public void setMetodoPago(String metodoPago) {
		this.metodoPago = metodoPago;
	}

	@Override
	public String toString() {
		return LABEL_PAY + "|" + getTerminosPago() + "|" + getNumeroCuentaBancaria() + "|" + getFechaVencimiento() + "|" + getMetodoPago() + "|";
	}

	
}
