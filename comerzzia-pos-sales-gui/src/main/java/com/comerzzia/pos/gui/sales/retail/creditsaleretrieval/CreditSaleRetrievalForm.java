
package com.comerzzia.pos.gui.sales.retail.creditsaleretrieval;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.validation.ValidationFormGui;

@Component
@Scope("prototype")
public class CreditSaleRetrievalForm extends ValidationFormGui{

	@NotEmpty (message = "Debe introducir el código de caja.")
	protected String tillCode;
	
	@NotEmpty (message = "Debe introducir el código de tienda.")
	protected String storeCode;
	
	@NotEmpty (message = "Debe introducir el código de identificación del documento.")
	protected String idDoc;
	
	@NotEmpty (message = "Debe introducir el código de identificación del documento.")
	protected String docType;
	
	public CreditSaleRetrievalForm(){
		
	}
	
	public CreditSaleRetrievalForm(String tillCode, String storeCode, String idDoc, String docType){
		
		this.tillCode = tillCode;
		this.storeCode = storeCode;
		this.idDoc = idDoc;
		this.docType = docType;
	}
	
	public String getTillCode() {
		return tillCode;
	}

	public void setTillCode(String tillCode) {
		this.tillCode = tillCode;
	}

	public String getStoreCode() {
		return storeCode;
	}

	public void setStoreCode(String storeCode) {
		this.storeCode = storeCode;
	}

	public String getIdDoc() {
		return idDoc;
	}

	public void setIdDoc(String idDoc) {
		this.idDoc = idDoc;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	@Override
	public void clearForm() {
		tillCode = "";
		storeCode = "";
		idDoc = "";
		docType = "";
	}

}
