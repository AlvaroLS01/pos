


package com.comerzzia.pos.gui.sales.country;

import javax.validation.constraints.Size;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.validation.ValidationFormGui;

@Component
@Scope("prototype")
public class CountryForm extends ValidationFormGui{
    
    @Size (max = 20)
    private String countryDes;
    
    @Size (max = 4)
    private String countryCode;
    
    public CountryForm(){
    }

    public CountryForm(String countryCode, String countryDes){
        
        this.countryDes = countryDes;
        this.countryCode = countryCode;
    }
    
    public String getCountryDes() {
        return countryDes;
    }

    public void setCountryDes(String countryDes) {
        this.countryDes = countryDes;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    @Override
    public void clearForm() {
        countryDes = "";
        countryCode = "";
    }
    
}
