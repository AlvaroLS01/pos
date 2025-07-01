


package com.comerzzia.pos.gui.sales.loyalcustomer.search;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.validation.ValidationFormGui;

@Component
@Scope("prototype")
public class LoyalCustomerSeachFormValidationBean extends ValidationFormGui{

    protected String cardNumber, document, name, lastName, phone, email;
        
    public LoyalCustomerSeachFormValidationBean() {
		
	}

	@Override
    public void clearForm() {
        cardNumber = "";
        document = "";
        name = "";
        lastName = "";
        phone = "";
        email = "";
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber.trim();
    }

	public String getDocument() {
		return document;
	}

	public void setDocument(String document) {
		this.document = document.trim();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name.trim();
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName.trim();
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone.trim();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email.trim();
	}
    
    
    
}

