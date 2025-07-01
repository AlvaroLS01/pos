

package com.comerzzia.pos.gui.sales.loyalcustomer;

import com.comerzzia.api.loyalty.client.model.LyCustomer;

import javafx.beans.property.SimpleStringProperty;


public class LoyalCustomerRow {
    
    protected SimpleStringProperty description;
    protected SimpleStringProperty vatNumber;
    protected SimpleStringProperty city;
    protected SimpleStringProperty province;

	protected LyCustomer lyCustomer;
    
    /**
     * Constructor de recuento
     * @param medioPago
     * @param cantidad
     * @param moneda 
     */
	public LoyalCustomerRow(LyCustomer lyCustomer) {
        this.lyCustomer = lyCustomer;
		this.description = new SimpleStringProperty(getFullName(lyCustomer) == null ? "" : getFullName(lyCustomer));
		this.vatNumber = new SimpleStringProperty(lyCustomer.getVatNumber() == null ? "" : lyCustomer.getVatNumber());
		this.city = new SimpleStringProperty(lyCustomer.getCity() == null ? "" : lyCustomer.getCity());
		this.province = new SimpleStringProperty(lyCustomer.getProvince() == null ? "" : lyCustomer.getProvince());
    }

	protected String getFullName(LyCustomer lyCustomer) {
		return lyCustomer.getName() + " " + lyCustomer.getLastName();
	}

    /**
     * @return la forma de pago
     */
    public String getDescription() {
        return description.get();
    }
    
    /**
     * @return la forma de pago
     */
    public SimpleStringProperty descriptionProperty() {
        return description;
    }
        /**
     * @return la forma de pago
     */
    public String getVatNumber() {
        return vatNumber.get();
    }
    
    /**
     * @return la forma de pago
     */
    public SimpleStringProperty vatNumberProperty() {
        return vatNumber;
    }
        /**
     * @return la forma de pago
     */
    public String getCity() {
        return city.get();
    }
    
    /**
     * @return la forma de pago
     */
    public SimpleStringProperty cityProperty() {
        return city;
    }
        /**
     * @return la forma de pago
     */
    public String getProvince() {
        return province.getValue();
    }
    
    /**
     * @return la forma de pago
     */
    public SimpleStringProperty provinceProperty() {
        return province;
    }
    
	public LyCustomer getLyCustomer() {
    	return lyCustomer;
    }
    
}
