


package com.comerzzia.pos.gui.sales.country;

import com.comerzzia.core.facade.model.Country;
import com.comerzzia.pos.core.gui.helper.HelperRow;

import javafx.beans.property.SimpleStringProperty;


public class CountryRow extends HelperRow<Country>{
    
    public CountryRow(Country country){
        
    	object = country;
        helperCode = new SimpleStringProperty(country.getCountryCode());
        helperDesc = new SimpleStringProperty(country.getCountryDes());
    }

    
}
