package com.comerzzia.pos.gui.sales.loyalcustomer.collectives;

import org.hibernate.validator.constraints.NotBlank;

import com.comerzzia.api.loyalty.client.model.Collective;

import javafx.beans.property.SimpleStringProperty;

public class LoyalCustomerCollectiveSelectionRow {

	@NotBlank
	protected SimpleStringProperty collectiveCode;
	
	@NotBlank
	protected SimpleStringProperty collectiveDes;
	
	public LoyalCustomerCollectiveSelectionRow(Collective collective){
		collectiveCode = new SimpleStringProperty(collective.getCollectiveCode());
		collectiveDes = new SimpleStringProperty(collective.getCollectiveDes());
	}

	public SimpleStringProperty collectiveCodeProperty() {
		return collectiveCode;
	}

	public String getCollectiveCode() {
		return collectiveCode.getValue();
	}

	public SimpleStringProperty collectiveDesProperty() {
		return collectiveDes;
	}
	
	public String getCollectiveDes() {
		return collectiveDes.getValue();
	}
	
	@Override
	public String toString(){
		return collectiveDes.getValue();
	}
	
}
