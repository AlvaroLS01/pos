package com.comerzzia.pos.gui.sales.loyalcustomer;

import com.comerzzia.core.facade.model.CountryIdType;

import javafx.beans.property.SimpleStringProperty;

public class LoyalCustomerCountryIdTypeRow {

	private SimpleStringProperty identTypeCode;
	
	private SimpleStringProperty identTypeDes;
		
	private CountryIdType countryIdType;
	
	public LoyalCustomerCountryIdTypeRow(){
		
	}
	
	public LoyalCustomerCountryIdTypeRow(CountryIdType countryIdType) {
		this.countryIdType = countryIdType;
		this.identTypeCode = new SimpleStringProperty(countryIdType.getIdentificationTypeCode());
		this.identTypeDes = new SimpleStringProperty(countryIdType.getIdentificationTypeDes());
	}

	public SimpleStringProperty identTypeCodeProperty() {
		return identTypeCode;
	}
	
	public String getIdentTypeCode(){
		return identTypeCode.get();
	}

	public void setIdentTypeCode(SimpleStringProperty identTypeCode) {
		this.identTypeCode = identTypeCode;
	}

	public SimpleStringProperty identTypeDesProperty() {
		return identTypeDes;
	}
	
	public String getIdentTypeDes(){
		return identTypeDes.get();
	}

	public void setIdentTypeDes(SimpleStringProperty identTypeDes) {
		this.identTypeDes = identTypeDes;
	}
	
	public CountryIdType getCountryIdType() {
		return countryIdType;
	}

	public String toString(){
		return identTypeDes.getValue();
	}
}
