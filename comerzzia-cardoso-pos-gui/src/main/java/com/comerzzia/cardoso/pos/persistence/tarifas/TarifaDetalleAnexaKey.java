package com.comerzzia.cardoso.pos.persistence.tarifas;

/**
 * GAP - DESCUENTO TARIFA
 */
public class TarifaDetalleAnexaKey{

	private String uidActividad;

	private String codtar;

	private String codart;

	public String getUidActividad(){
		return uidActividad;
	}

	public void setUidActividad(String uidActividad){
		this.uidActividad = uidActividad == null ? null : uidActividad.trim();
	}

	public String getCodtar(){
		return codtar;
	}

	public void setCodtar(String codtar){
		this.codtar = codtar == null ? null : codtar.trim();
	}

	public String getCodart(){
		return codart;
	}

	public void setCodart(String codart){
		this.codart = codart == null ? null : codart.trim();
	}
}