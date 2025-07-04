package com.comerzzia.cardoso.pos.persistence.lotes.anexa;

/**
 * GAP - PERSONALIZACIONES V3 - LOTES
 */
public class CardosoLoteArticuloBean extends CardosoLoteArticuloKey{

	private String lote;

	public String getLote(){
		return lote;
	}

	public void setLote(String lote){
		this.lote = lote == null ? null : lote.trim();
	}

}