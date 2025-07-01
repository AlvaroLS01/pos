

package com.comerzzia.pos.gui.sales.retail.invoice;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.validation.ValidationFormGui;

@Component
@Scope("prototype")
public class InvoiceDataForm extends ValidationFormGui {

	@NotEmpty (message = "Debe rellenar el campo del nombre comercial del cliente.")
    @Size(max = 45, message = "La longitud del campo nombre comercial no puede superar los {max} caracteres")
    protected String businessName;
	
    @Size(max = 50, message = "La longitud del campo domicilio no puede superar los {max} caracteres")
    @NotEmpty (message = "Debe rellenar el campo domicilio.")
    protected String address;
    
    @Size(max = 50, message = "La longitud del campo población no puede superar los {max} caracteres")
    protected String city;

    @Size(max = 50, message = "La longitud del campo provincia no puede superar los {max} caracteres")
    protected String province;
    
    @Size(max = 50, message = "La longitud del campo localidad no puede superar los {max} caracteres")
    protected String location;

    @Size(max = 8, message = "La longitud del campo código postal no puede superar los {max} caracteres")
    protected String postalCode;
    
    @NotEmpty (message = "Debe rellenar el campo de identificación del cliente.")
    @Size(max = 20, message = "La longitud del campo número de documento no puede superar los {max} caracteres")
    protected String vatNumber;

    @Size(max = 15, message = "La longitud del campo teléfono no puede superar los {max} caracteres")
    protected String phone;
     
    @NotEmpty (message = "Debe seleccionar el país del cliente.")
    protected String country;
    
    @Size(max = 45, message = "La longitud del campo no puede superar los 45 caracteres.")
    protected String bank;
    @Size(max = 50, message = "La longitud del campo no puede superar los 50 caracteres.")
    protected String addressBank;
    @Size(max = 50, message = "La longitud del campo no puede superar los 50 caracteres.")
    protected String cityBank;
    @Size(max = 20, message = "La longitud del campo no puede superar los 20 caracteres.")
    protected String ibanBank;

    public InvoiceDataForm() {

    }

    public InvoiceDataForm(String country, String address, String province, String vatNumber, 
            String postalCode, String phone, String city, String businessName) {

        this.vatNumber = vatNumber;
        this.postalCode = postalCode;
        this.address = address;
        this.businessName = businessName;
        this.phone = phone;
        this.city = city;
        this.province = province;
        this.country = country;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
    
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getVatNumber() {
        return vatNumber;
    }

    public void setVatNumber(String vatNumber) {
        this.vatNumber = vatNumber;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getBank() {
		return bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}

	public String getAddressBank() {
		return addressBank;
	}

	public void setAddressBank(String addressBank) {
		this.addressBank = addressBank;
	}

	public String getCityBank() {
		return cityBank;
	}

	public void setCityBank(String cityBank) {
		this.cityBank = cityBank;
	}

	public String getIbanBank() {
		return ibanBank;
	}

	public void setIbanBank(String ibanBank) {
		this.ibanBank = ibanBank;
	}

	@Override
    public void clearForm() {
    	businessName = "";
        address = "";
        city = "";
        province = "";
        location = "";
        postalCode = "";
        vatNumber = "";
        phone = "";
        country = "";        
        
        bank = "";
        addressBank = "";
        cityBank = "";
        ibanBank = "";
    }

}
