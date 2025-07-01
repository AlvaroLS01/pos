
package com.comerzzia.pos.gui.sales.customer;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.validation.ValidationFormGui;

@Component
@Scope("prototype")
public class CentralCustomerSearchForm extends ValidationFormGui{
	
	@NotEmpty(message = "Debe rellenar el campo documento")
	protected String customerCode;

	public CentralCustomerSearchForm() {
	}

	public String getCustomerCode() {
		return customerCode;
	}


	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}


	@Override
	public void clearForm() {
		customerCode = "";
		
	}

}
