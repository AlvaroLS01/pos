package com.comerzzia.bimbaylola.pos.services.vertex;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class LineaVertex {
	
	@XmlElement(name = "document_type")
	private String documentType;
	
	@XmlElement(name = "document_no")
	private String documentNumber;
	
	@XmlElement(name = "line_no")
	private String lineNumber;
	
	@XmlElement(name = "amount")
	private String amount;
	
	@XmlElement(name = "amount_including_tax")
	private String amountWithTax;
	
	@XmlElement(name = "tax_group_code")
	private String taxGroupCode;

	
	public String getDocumentType() {
		return documentType;
	}

	
	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	
	public String getDocumentNumber() {
		return documentNumber;
	}

	
	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}

	
	public String getLineNumber() {
		return lineNumber;
	}

	
	public void setLineNumber(String lineNumber) {
		this.lineNumber = lineNumber;
	}

	
	public String getAmount() {
		return amount;
	}

	
	public void setAmount(String amount) {
		this.amount = amount;
	}

	
	public String getAmountWithTax() {
		return amountWithTax;
	}

	
	public void setAmountWithTax(String amountWithTax) {
		this.amountWithTax = amountWithTax;
	}

	
	public String getTaxGroupCode() {
		return taxGroupCode;
	}

	
	public void setTaxGroupCode(String taxGroupCode) {
		this.taxGroupCode = taxGroupCode;
	}
	
	

}
