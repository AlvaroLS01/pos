package com.comerzzia.cardoso.pos.persistence.lotes.anexa;

/**
 * GAP - PERSONALIZACIONES V3 - LOTES
 */
public class CardosoLoteArticuloKey{

	private String uidActividad;

	private String codart;

	public String getUidActividad(){
		return uidActividad;
	}

	public void setUidActividad(String uidActividad){
		this.uidActividad = uidActividad == null ? null : uidActividad.trim();
	}

	public String getCodart(){
		return codart;
	}

	public void setCodart(String codart){
		this.codart = codart == null ? null : codart.trim();
	}
}