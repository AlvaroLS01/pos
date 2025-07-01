package com.comerzzia.pos.gui.sales.loyalcustomer.stores;

import javax.validation.constraints.Size;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.validation.ValidationFormGui;

@Component
@Scope("prototype")
public class LoyalCustomerPreferredStoreFormValidationBean  extends ValidationFormGui{
	
	@Size (max = 20)
    private String storeDes;
    
    @Size (max = 4)
    private String storeCode;
    
    public LoyalCustomerPreferredStoreFormValidationBean(){
    	
    }

    public LoyalCustomerPreferredStoreFormValidationBean(String storeCode, String storeDes){
    	this.storeCode = storeCode;
    	this.storeDes = storeDes;
    }

	public String getStoreDes() {
		return storeDes;
	}

	public void setStoreDes(String storeDes) {
		this.storeDes = storeDes;
	}

	public String getStoreCode() {
		return storeCode;
	}

	public void setStoreCode(String storeCode) {
		this.storeCode = storeCode;
	}

	@Override
	public void clearForm() {
		storeDes = "";
		storeCode = "";
	}

}
