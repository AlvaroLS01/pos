package com.comerzzia.cardoso.pos.persistence.agente;

/**
 * GAP - AGENTES 
 */
public class ClienteAgenteKey{

	private String codcli;

	private String codage;

	public String getCodcli(){
		return codcli;
	}

	public void setCodcli(String codcli){
		this.codcli = codcli == null ? null : codcli.trim();
	}

	public String getCodage(){
		return codage;
	}

	public void setCodage(String codage){
		this.codage = codage == null ? null : codage.trim();
	}
}