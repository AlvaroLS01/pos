package com.comerzzia.dinosol.pos.services.ventas.reparto.dto;

public class ServicioRepartoDto {

	private String nombre;

	private String codTar;

	private String numeroTarjeta;

	private String color;

	private String icono;
	
	private String tarjetaFidelizado;

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getCodTar() {
		return codTar;
	}

	public void setCodTar(String codTar) {
		this.codTar = codTar;
	}

	public String getNumeroTarjeta() {
		return numeroTarjeta;
	}

	public void setNumeroTarjeta(String numeroTarjeta) {
		this.numeroTarjeta = numeroTarjeta;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getIcono() {
		return icono;
	}

	public void setIcono(String icono) {
		this.icono = icono;
	}

	public String getTarjetaFidelizado() {
		return tarjetaFidelizado;
	}

	public void setTarjetaFidelizado(String tarjetaFidelizado) {
		this.tarjetaFidelizado = tarjetaFidelizado;
	}

}
