package com.comerzzia.pos.gui.sales.loyalcustomer;

import com.comerzzia.api.loyalty.client.model.CivilStatus;

import javafx.beans.property.SimpleStringProperty;

public class LoyalCustomerCivilStatusRow {

	protected SimpleStringProperty civilStatusCode;
	
	protected SimpleStringProperty civilStatusDes;
	
	public LoyalCustomerCivilStatusRow(CivilStatus civilStatus){
		this.civilStatusCode = new SimpleStringProperty(civilStatus.getCivilStatusCode() != null ? civilStatus.getCivilStatusCode() : "");
		this.civilStatusDes = new SimpleStringProperty(civilStatus.getCivilStatusDes() != null ? civilStatus.getCivilStatusDes() : "");
	}

	public SimpleStringProperty civilStatusCodeProperty() {
		return civilStatusCode;
	}

	public void setCivilStatusCode(SimpleStringProperty civilStatusCode) {
		this.civilStatusCode = civilStatusCode;
	}
	
	public void setCivilStatusCode(String civilStatusCode) {
		this.civilStatusCode = new SimpleStringProperty(civilStatusCode);
	}

	public SimpleStringProperty civilStatusDesProperty() {
		return civilStatusDes;
	}

	public void setCivilStatusDes(SimpleStringProperty civilStatusDes) {
		this.civilStatusDes = civilStatusDes;
	}
	
	public void setCivilStatusDes(String civilStatusDes) {
		this.civilStatusDes = new SimpleStringProperty(civilStatusDes);
	}
	
	public String getCivilStatusCode(){
		return civilStatusCode.get();
	}
	
	public String getCivilStatusDes(){
		return civilStatusDes.get();
	}
	
	@Override
	public String toString(){
		return civilStatusDes.getValue();
	}
	
}
