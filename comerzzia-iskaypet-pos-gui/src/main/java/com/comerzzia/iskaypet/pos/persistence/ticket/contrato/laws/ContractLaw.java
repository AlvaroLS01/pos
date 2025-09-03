package com.comerzzia.iskaypet.pos.persistence.ticket.contrato.laws;

public class ContractLaw {
	private String activityId;
	private String codCountry;
	private String orden;
	private String law;
	
	
	public ContractLaw() {
	}

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public String getCodCountry() {
		return codCountry;
	}

	public void setCodCountry(String codCountry) {
		this.codCountry = codCountry;
	}

	public String getOrden() {
		return orden;
	}

	public void setOrden(String orden) {
		this.orden = orden;
	}

	public String getLaw() {
		return law;
	}

	public void setLaw(String law) {
		this.law = law;
	}

}
