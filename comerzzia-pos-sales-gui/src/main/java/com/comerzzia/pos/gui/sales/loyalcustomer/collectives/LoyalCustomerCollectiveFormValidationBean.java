package com.comerzzia.pos.gui.sales.loyalcustomer.collectives;

import javax.validation.constraints.Size;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.validation.ValidationFormGui;

@Component
@Scope("prototype")
public class LoyalCustomerCollectiveFormValidationBean extends ValidationFormGui{

	@Size (max = 255)
    protected String collectiveDes;
    
    @Size (max = 40)
    protected String collectiveCode;
    
    public LoyalCustomerCollectiveFormValidationBean(){
    	
    }

    public LoyalCustomerCollectiveFormValidationBean(String collectiveCode, String collectiveDes){
    	this.collectiveCode = collectiveCode;
    	this.collectiveDes = collectiveDes;
    }

	public String getCollectiveDes() {
		return collectiveDes;
	}

	public void setCollectiveDes(String collectiveDes) {
		this.collectiveDes = collectiveDes;
	}

	public String getCollectiveCode() {
		return collectiveCode;
	}

	public void setCollectiveCode(String collectiveCode) {
		this.collectiveCode = collectiveCode;
	}

	@Override
	public void clearForm() {
		collectiveDes = "";
		collectiveCode = "";
	}

}
