package com.comerzzia.cardoso.pos.persistence.balanza;

/**
 * GAP - PERSONALIZACIONES V3 - INTEGRACIÓN BALANZA BIZERBA
 */
public class TicketBalanzaKey{

	private String uidActividad;

	private String uidTicketBalanza;

	public String getUidActividad(){
		return uidActividad;
	}

	public void setUidActividad(String uidActividad){
		this.uidActividad = uidActividad == null ? null : uidActividad.trim();
	}

	public String getUidTicketBalanza(){
		return uidTicketBalanza;
	}

	public void setUidTicketBalanza(String uidTicketBalanza){
		this.uidTicketBalanza = uidTicketBalanza == null ? null : uidTicketBalanza.trim();
	}
}