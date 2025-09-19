package com.comerzzia.bimbaylola.pos.services.vertex;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class LineaDetailVertex {
	
	@XmlElement(name = "document_type")
	private String documentType;
	
	@XmlElement(name = "document_no")
	private String documentNumber;
	
	@XmlElement(name = "line_no")
	private String lineNumber;

	@XmlElement(name = "tax_line_no")
	private String taxLineNo;

	@XmlElement(name = "tax_type_id")
	private String taxTypeId;

	@XmlElement(name = "tax_amount")
	private String taxAmount;

	@XmlElement(name = "tax_percentage")
	private String taxPercentage;

	@XmlElement(name = "jurisdiction_name")
	private String jurisdictionName;

	@XmlElement(name = "tax_type_name")
	private String taxTypeName;

	
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

	
	public String getTaxLineNo() {
		return taxLineNo;
	}

	
	public void setTaxLineNo(String taxLineNo) {
		this.taxLineNo = taxLineNo;
	}

	
	public String getTaxTypeId() {
		return taxTypeId;
	}

	
	public void setTaxTypeId(String taxTypeId) {
		this.taxTypeId = taxTypeId;
	}

	
	public String getTaxAmount() {
		return taxAmount;
	}

	
	public void setTaxAmount(String taxAmount) {
		this.taxAmount = taxAmount;
	}

	
	public String getTaxPercentage() {
		return taxPercentage;
	}

	
	public void setTaxPercentage(String taxPercentage) {
		this.taxPercentage = taxPercentage;
	}

	
	public String getJurisdictionName() {
		return jurisdictionName;
	}

	
	public void setJurisdictionName(String jurisdictionName) {
		this.jurisdictionName = jurisdictionName;
	}

	
	public String getTaxTypeName() {
		return taxTypeName;
	}

	
	public void setTaxTypeName(String taxTypeName) {
		this.taxTypeName = taxTypeName;
	}
	
	

}
