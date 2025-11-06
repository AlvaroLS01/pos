package com.comerzzia.dinosol.pos.services.ticket.lineas;

import java.math.BigDecimal;
import java.util.Date;

public class TarjetaRegaloDto {

	public static String ESTADO_PENDIENTE_ACTIVAR = "PENDIENTE DE ACTIVAR";
	public static String ESTADO_ANULADA = "ANULADA";
	public static String ESTADO_ACTIVA = "ACTIVA";
	
	private Long idTarjeta;

	private String numeroTarjeta;

	private BigDecimal saldo;

	private String estado;

	private Date fechaCaducidad;
	
	private String pin;

	public TarjetaRegaloDto() {
		estado = ESTADO_PENDIENTE_ACTIVAR;
	}

	public Long getIdTarjeta() {
		return idTarjeta;
	}

	public void setIdTarjeta(Long idTarjeta) {
		this.idTarjeta = idTarjeta;
	}

	public String getNumeroTarjeta() {
		return numeroTarjeta;
	}

	public void setNumeroTarjeta(String numeroTarjeta) {
		this.numeroTarjeta = numeroTarjeta;
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

	public Date getFechaCaducidad() {
		return fechaCaducidad;
	}

	public void setFechaCaducidad(Date fechaCaducidad) {
		this.fechaCaducidad = fechaCaducidad;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

}
