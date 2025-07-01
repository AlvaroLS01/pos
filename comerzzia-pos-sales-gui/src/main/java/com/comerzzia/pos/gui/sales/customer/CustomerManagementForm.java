package com.comerzzia.pos.gui.sales.customer;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.validation.ValidationFormGui;

@Component
@Scope("prototype")
public class CustomerManagementForm extends ValidationFormGui {

    @NotNull(message = "Debe seleccionar un código de tratamiento de impuestos.")
    @NotEmpty(message = "Debe seleccionar un código de tratamiento de impuestos.")
    protected String taxTreatId;
    @NotNull(message = "Debe rellenar el campo descripción.")
    @NotEmpty(message = "Debe rellenar el campo descripción.")
    @Size(max = 45, message = "La longitud del campo no puede superar los 45 caracteres.")
    protected String description;
    /**
     * Máximo 11 porque lo usaremos como CODCLI.
     */
    @NotEmpty(message = "Debe rellenar el campo de identificación del cliente.")
    @Size(max = 11, message = "La longitud del campo número de documento no puede superar los {max} caracteres")
    protected String vatNumber;
    @Size(max = 15, message = "La longitud del campo no puede superar los 15 caracteres.")
    protected String phone2;

    @Size(max = 15, message = "La longitud del campo no puede superar los 15 caracteres.")
    protected String fax;

    @NotEmpty(message = "Debe rellenar el campo del nombre comercial del cliente.")
    @Size(max = 45, message = "La longitud del campo nombre comercial no puede superar los {max} caracteres")
    protected String companyName;
    @Size(max = 50, message = "La longitud del campo domicilio no puede superar los {max} caracteres")
    @NotEmpty(message = "Debe rellenar el campo domicilio.")
    protected String address;
    @Size(max = 50, message = "La longitud del campo población no puede superar los {max} caracteres")
    protected String city;
    @Size(max = 50, message = "La longitud del campo provincia no puede superar los {max} caracteres")
    protected String province;
    @Size(max = 50, message = "La longitud del campo localidad no puede superar los {max} caracteres")
    protected String location;
    @Size(max = 8, message = "La longitud del campo código postal no puede superar los {max} caracteres")
    protected String postalCode;
    @Size(max = 15, message = "La longitud del campo teléfono no puede superar los {max} caracteres")
    protected String phone;
    @NotEmpty(message = "Debe seleccionar el país del cliente.")
    protected String country;
    @Size(max = 45, message = "La longitud del campo no puede superar los {max} caracteres.")
    protected String bank;
    @Size(max = 50, message = "La longitud del campo no puede superar los {max} caracteres.")
    protected String addressBank;
    @Size(max = 50, message = "La longitud del campo no puede superar los {max} caracteres.")
    protected String cityBank;
    @Size(max = 20, message = "La longitud del campo no puede superar los {max} caracteres.")
    protected String ibanBank;    
    @Size(max= 255, message = "La longitud del campo no puede superar los {max} caracteres.")
    protected String remarks;
    @Size(max= 60, message = "La longitud del campo no puede superar los {max} caracteres.")
    protected String email;

    public CustomerManagementForm() {}

    public void clearForm() {
        taxTreatId = "";
        description = "";
        phone2 = "";
        fax = "";

        companyName = "";
        address = "";
        city = "";
        province = "";
        location = "";
        postalCode = "";
        vatNumber = "";
        phone = "";
        country = "";
        email = "";
        
        bank = "";
        addressBank = "";
        cityBank = "";
        ibanBank = "";
        
        remarks = "";
    }

    public String getVatNumber() {
        return vatNumber;
    }

    public void setVatNumber(String vatNumber) {
        this.vatNumber = vatNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTaxTreatId() {
        return taxTreatId;
    }

    public void setTaxTreatId(String taxTreatId) {
        this.taxTreatId = taxTreatId;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public void setFax(String fax) {
        this.fax = fax;
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

    public void setIbanBank(String cccBank) {
        this.ibanBank = cccBank;
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

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getPhone2() {
		return phone2;
	}

	public String getFax() {
		return fax;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
