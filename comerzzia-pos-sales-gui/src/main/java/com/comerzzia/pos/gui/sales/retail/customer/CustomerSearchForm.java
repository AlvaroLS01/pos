

package com.comerzzia.pos.gui.sales.retail.customer;

import javax.validation.constraints.Size;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.validation.ValidationFormGui;

@Component
@Scope("prototype")
public class CustomerSearchForm extends ValidationFormGui {

    @Size(max = 45)
    protected String customerDes;

    @Size(max = 20, message = "La longitud del campo no puede superar los 20 caracteres.")
    protected String identTypeCode;

    public CustomerSearchForm() {

    }

    public CustomerSearchForm(String customerDes, String identTypeCode) {
        this.identTypeCode = identTypeCode;
        this.customerDes = customerDes;
    }

    public String getCustomerDes() {
        return customerDes;
    }

    public void setCustomerDes(String customerDes) {
        this.customerDes = customerDes;
    }

    public String getIdentTypeCode() {
        return identTypeCode;
    }

    public void setIdentTypeCode(String identTypeCode) {
        this.identTypeCode = identTypeCode;
    }

    @Override
    public void clearForm() {
    	identTypeCode = "";
        customerDes = "";
    }

}
