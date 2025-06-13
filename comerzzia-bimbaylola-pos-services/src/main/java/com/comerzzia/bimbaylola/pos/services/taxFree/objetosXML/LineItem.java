package com.comerzzia.bimbaylola.pos.services.taxFree.objetosXML;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "line_item")
@Component
@Primary
@Scope("prototype")
public class LineItem {

	@XmlElement(name = "item_number")
	protected int item_number;

	@XmlElement(name = "item_description")
	protected String item_description;

	@XmlElement(name = "item_vat_rate")
	protected BigDecimal item_vat_rate;

	@XmlElement(name = "item_net_amount")
	protected BigDecimal item_net_amount;

	@XmlElement(name = "item_gross_amount")
	protected BigDecimal item_gross_amount;

	@XmlElement(name = "item_vat_amount")
	protected BigDecimal item_vat_amount;

	@XmlElement(name = "individual_item_value")
	protected BigDecimal individual_item_value;

	@XmlElement(name = "department_id")
	protected String department_id;

	@XmlElement(name = "item_quantity")
	protected int item_quantity;

	@XmlElement(name = "line_item_eligible")
	protected boolean line_item_eligible;

	@XmlElement(name = "extra_information")
	protected ExtraInformation extra_information;

	@XmlElement(name = "original_invoice_barcode_data")
	protected Object original_invoice_barcode_data;

	public int getItem_number() {
		return item_number;
	}

	public void setItem_number(int item_number) {
		this.item_number = item_number;
	}

	public String getItem_description() {
		return item_description;
	}

	public void setItem_description(String item_description) {
		this.item_description = item_description;
	}

	public BigDecimal getItem_vat_rate() {
		return item_vat_rate;
	}

	public void setItem_vat_rate(BigDecimal item_vat_rate) {
		this.item_vat_rate = item_vat_rate;
	}

	public BigDecimal getItem_net_amount() {
		return item_net_amount;
	}

	public void setItem_net_amount(BigDecimal item_net_amount) {
		this.item_net_amount = item_net_amount;
	}

	public BigDecimal getItem_gross_amount() {
		return item_gross_amount;
	}

	public void setItem_gross_amount(BigDecimal item_gross_amount) {
		this.item_gross_amount = item_gross_amount;
	}

	public BigDecimal getItem_vat_amount() {
		return item_vat_amount;
	}

	public void setItem_vat_amount(BigDecimal item_vat_amount) {
		this.item_vat_amount = item_vat_amount;
	}

	public BigDecimal getIndividual_item_value() {
		return individual_item_value;
	}

	public void setIndividual_item_value(BigDecimal individual_item_value) {
		this.individual_item_value = individual_item_value;
	}

	public String getDepartment_id() {
		return department_id;
	}

	public void setDepartment_id(String department_id) {
		this.department_id = department_id;
	}

	public int getItem_quantity() {
		return item_quantity;
	}

	public void setItem_quantity(int item_quantity) {
		this.item_quantity = item_quantity;
	}

	public boolean isLine_item_eligible() {
		return line_item_eligible;
	}

	public void setLine_item_eligible(boolean line_item_eligible) {
		this.line_item_eligible = line_item_eligible;
	}

	public ExtraInformation getExtra_information() {
		return extra_information;
	}

	public void setExtra_information(ExtraInformation extra_information) {
		this.extra_information = extra_information;
	}

	public Object getOriginal_invoice_barcode_data() {
		return original_invoice_barcode_data;
	}

	public void setOriginal_invoice_barcode_data(Object original_invoice_barcode_data) {
		this.original_invoice_barcode_data = original_invoice_barcode_data;
	}

}
