package com.comerzzia.bimbaylola.pos.services.taxFree.objetosXML;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "transaction_header")
@Component
@Primary
@Scope("prototype")
public class TransactionHeader {

	@XmlElement(name = "transaction_type")
	protected int transaction_type;
	
	@XmlElement(name = "transaction_date")
	protected String transaction_date;
	
	@XmlElement(name = "transaction_time")
	protected String transaction_time;
	
	@XmlElement(name = "invoice_number")
	protected String invoice_number;
	
	@XmlElement(name = "barcode_data")
	protected String barcode_data;
	
	@XmlElement(name = "fiscal_number")
	protected String fiscal_number;
	
	@XmlElement(name = "fiscal_register_number")
	protected String fiscal_register_number;
	
	@XmlElement(name = "number_of_items")
	protected int number_of_items;

	public int getTransaction_type() {
		return transaction_type;
	}

	public void setTransaction_type(int transaction_type) {
		this.transaction_type = transaction_type;
	}

	public String getTransaction_date() {
		return transaction_date;
	}

	public void setTransaction_date(String transaction_date) {
		this.transaction_date = transaction_date;
	}

	public String getTransaction_time() {
		return transaction_time;
	}

	public void setTransaction_time(String transaction_time) {
		this.transaction_time = transaction_time;
	}

	public String getInvoice_number() {
		return invoice_number;
	}

	public void setInvoice_number(String invoice_number) {
		this.invoice_number = invoice_number;
	}

	public String getBarcode_data() {
		return barcode_data;
	}

	public void setBarcode_data(String barcode_data) {
		this.barcode_data = barcode_data;
	}

	public String getFiscal_number() {
		return fiscal_number;
	}

	public void setFiscal_number(String fiscal_number) {
		this.fiscal_number = fiscal_number;
	}

	public String getFiscal_register_number() {
		return fiscal_register_number;
	}

	public void setFiscal_register_number(String fiscal_register_number) {
		this.fiscal_register_number = fiscal_register_number;
	}

	public int getNumber_of_items() {
		return number_of_items;
	}

	public void setNumber_of_items(int number_of_items) {
		this.number_of_items = number_of_items;
	}

}
