package com.comerzzia.bimbaylola.pos.services.vertex;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class CabeceraVertex {
	
	@XmlElement(name = "document_type")
	private String documentType;
	
	@XmlElement(name = "document_no")
	private String documentNumber;
	
	@XmlElement(name = "amount")
	private String amount;
	
	@XmlElement(name = "amount_including_tax")
	private String amountWithTax;
	
	@XmlElement(name = "tax_area_id")
	private String taxAreaId;
	
	@XmlElement(name = "is_sent_to_vertex")
	private String isSentToVertex;
	
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

	
	public String getTaxAreaId() {
		return taxAreaId;
	}

	
	public void setTaxAreaId(String taxAreaId) {
		this.taxAreaId = taxAreaId;
	}

	
	public String getIsSentToVertex() {
		return isSentToVertex;
	}

	
	public void setIsSentToVertex(String isSentToVertex) {
		this.isSentToVertex = isSentToVertex;
	}

}
