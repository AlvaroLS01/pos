package com.comerzzia.pos.gui.sales.loyalcustomer.search.select;

import org.apache.commons.lang3.StringUtils;

import com.comerzzia.api.loyalty.client.model.LyCustomer;

import javafx.beans.property.SimpleStringProperty;

public class LoyalCustomerSelectRow {

	protected SimpleStringProperty lyCustomerCode;
	protected SimpleStringProperty name;
	protected SimpleStringProperty lastName;
	protected SimpleStringProperty address;
	protected SimpleStringProperty card;
	protected SimpleStringProperty document;

	private LyCustomer customer;

	public LoyalCustomerSelectRow(LyCustomer customer) {

		this.customer = customer;
		lyCustomerCode = new SimpleStringProperty(customer.getLyCustomerCode());
		name = new SimpleStringProperty(customer.getName());
		lastName = new SimpleStringProperty(customer.getLastName());
		address = new SimpleStringProperty("");
		card = new SimpleStringProperty("");
		if (StringUtils.isNotBlank(customer.getCardNumber())) {
			card = new SimpleStringProperty(customer.getCardNumber());
		}
		if (StringUtils.isNotBlank(customer.getAddress())) {
			address = new SimpleStringProperty(customer.getAddress());
			if (StringUtils.isNotBlank(customer.getCity())) {
				address = new SimpleStringProperty(customer.getAddress() + ", " + customer.getCity());
			}
		}
		document = new SimpleStringProperty(customer.getVatNumber());
	}

	public SimpleStringProperty getLyCustomerCode() {
		return lyCustomerCode;
	}

	public void setLyCustomerCode(SimpleStringProperty lyCustomerCode) {
		this.lyCustomerCode = lyCustomerCode;
	}

	public SimpleStringProperty getName() {
		return name;
	}

	public void setName(SimpleStringProperty name) {
		this.name = name;
	}

	public SimpleStringProperty getLastName() {
		return lastName;
	}

	public void setLastName(SimpleStringProperty lastName) {
		this.lastName = lastName;
	}

	public SimpleStringProperty getAddress() {
		return address;
	}

	public void setAddress(SimpleStringProperty address) {
		this.address = address;
	}

	public SimpleStringProperty getCard() {
		return card;
	}

	public void setCard(SimpleStringProperty card) {
		this.card = card;
	}

	public SimpleStringProperty getDocument() {
		return document;
	}

	public void setDocument(SimpleStringProperty document) {
		this.document = document;
	}

	public LyCustomer getLyCustomer() {
		return customer;
	}

}
