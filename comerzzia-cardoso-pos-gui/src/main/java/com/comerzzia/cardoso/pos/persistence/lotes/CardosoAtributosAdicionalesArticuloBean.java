package com.comerzzia.cardoso.pos.persistence.lotes;

/**
 * GAP - PERSONALIZACIONES V3 - LOTES
 * GAP - PERSONALIZACIONES V3 - ARTÍCULOS PELIGROSOS
 */
public class CardosoAtributosAdicionalesArticuloBean extends CardosoAtributosAdicionalesArticuloKey{

	private String peligroso;
	private String lote;

	public String getPeligroso(){
		return peligroso;
	}

	public void setPeligroso(String peligroso){
		this.peligroso = peligroso == null ? null : peligroso.trim();
	}

	public Boolean getLote(){
		return lote.equals("S");
	}

	public void setLote(String lote){
		this.lote = lote;
	}

}