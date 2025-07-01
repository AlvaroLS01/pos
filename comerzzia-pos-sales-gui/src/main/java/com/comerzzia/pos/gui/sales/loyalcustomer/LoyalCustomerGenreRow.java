package com.comerzzia.pos.gui.sales.loyalcustomer;

import javafx.beans.property.SimpleStringProperty;

public class LoyalCustomerGenreRow {

	private SimpleStringProperty code;
	
	private SimpleStringProperty value;
	
	public LoyalCustomerGenreRow(){
		
	}
	
	public LoyalCustomerGenreRow(String code, String value){
		this.code = new SimpleStringProperty(code);
		this.value = new SimpleStringProperty(value);
	}

	public SimpleStringProperty codeProperty() {
		return code;
	}
	
	public String getCode(){
		return code.get();
	}

	public void setCode(SimpleStringProperty code) {
		this.code = code;
	}

	public SimpleStringProperty valueProperty() {
		return value;
	}
	
	public String getValue(){
		return value.get();
	}

	public void setValue(SimpleStringProperty value) {
		this.value = value;
	}
	
	public String toString(){
		return value.getValue();
	}
}
