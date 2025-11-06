package com.comerzzia.dinosol.pos.gui.ventas.tickets.dto;

import com.comerzzia.pos.services.ticket.lineas.LineaTicket;

public class LecturaTarjetaBpDto extends LineaTicket {

	/**
     * 
     */
    private static final long serialVersionUID = 7615337008106647944L;
    
	private String numeroTarjeta;

	public LecturaTarjetaBpDto(String numeroTarjeta) {
		super();
		this.numeroTarjeta = numeroTarjeta;
	}

	public String getNumeroTarjeta() {
		return numeroTarjeta;
	}

	public void setNumeroTarjeta(String numeroTarjeta) {
		this.numeroTarjeta = numeroTarjeta;
	}

}
