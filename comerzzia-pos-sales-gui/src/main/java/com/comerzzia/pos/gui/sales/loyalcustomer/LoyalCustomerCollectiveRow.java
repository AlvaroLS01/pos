package com.comerzzia.pos.gui.sales.loyalcustomer;

import com.comerzzia.api.loyalty.client.model.Collective;

import javafx.beans.property.SimpleStringProperty;

public class LoyalCustomerCollectiveRow {

	private SimpleStringProperty collectiveCode;
	
	private SimpleStringProperty collectiveDes;
	
	private SimpleStringProperty collectiveType;
	
	public LoyalCustomerCollectiveRow(Collective collective){
		collectiveCode = new SimpleStringProperty(collective.getCollectiveCode() != null ? collective.getCollectiveCode() : "");
		collectiveDes = new SimpleStringProperty(collective.getCollectiveDes() != null ? collective.getCollectiveDes() : "");
		collectiveType = new SimpleStringProperty(collective.getType() != null ? collective.getType().getCollectiveTypeDes() : "");
	}

	public SimpleStringProperty propertyCollectiveCode() {
		return collectiveCode;
	}
	
	public String getCollectiveCode(){
		return collectiveCode.get();
	}

	public SimpleStringProperty propertyCollectiveDes() {
		return collectiveDes;
	}
	
	public String getCollectiveDes(){
		return collectiveDes.get();
	}

	public SimpleStringProperty propertyCollectiveType(){
		return collectiveType;
	}

	public String getCollectiveType(){
		return collectiveType.get();
	}
	
}
