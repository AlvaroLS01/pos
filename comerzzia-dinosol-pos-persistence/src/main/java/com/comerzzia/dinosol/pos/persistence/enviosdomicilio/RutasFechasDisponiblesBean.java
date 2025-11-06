package com.comerzzia.dinosol.pos.persistence.enviosdomicilio;

public class RutasFechasDisponiblesBean {
	
	private String fecha;
	private String tramoHorario;
	private String fechaOrigen;
	
	public String getFecha() {
		return fecha;
	}
	
	public String getTramoHorario() {
		return tramoHorario;
	}
	
	public String getFechaOrigen() {
		return fechaOrigen;
	}
	
	public void setFechaOrigen(String fechaOrigen) {
		/* Dividimos la fecha recibida y completamos las variables */
		String arrayFecha[] = fechaOrigen.split("T");
		
		String arrayDay[] = arrayFecha[0].split("-");
		String day = arrayDay[2] + "/" + arrayDay[1] + "/" + arrayDay[0];
		
		String arrayHour[] = arrayFecha[1].split("\\+");
		
		fecha = day;
		tramoHorario = arrayHour[0];
		this.fechaOrigen = fechaOrigen;
	}
	
}
