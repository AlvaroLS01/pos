

package com.comerzzia.pos.gui.sales.retail.customer;

import com.comerzzia.omnichannel.facade.model.sale.Customer;

import javafx.beans.property.SimpleStringProperty;


public class CustomerDto {
    
	protected SimpleStringProperty description;
	protected SimpleStringProperty vatNumber;
    protected SimpleStringProperty city;
    protected SimpleStringProperty province;

    protected Customer customer;
    
    /**
     * Constructor de recuento
     * @param medioPago
     * @param cantidad
     * @param moneda 
     */
    public CustomerDto (Customer customer){
        this.customer = customer;
        this.description = new SimpleStringProperty(customer.getCustomerDes()== null? "":customer.getCustomerDes());
        this.vatNumber = new SimpleStringProperty(customer.getVatNumber()== null? "":customer.getVatNumber());
        this.city = new SimpleStringProperty(customer.getCity()== null? "":customer.getCity());
        this.province = new SimpleStringProperty(customer.getProvince()== null? "":customer.getProvince());
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
    
    public Customer getCustomer(){
    	return customer;
    }
    
}
