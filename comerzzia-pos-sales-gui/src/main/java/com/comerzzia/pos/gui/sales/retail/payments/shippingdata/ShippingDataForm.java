package com.comerzzia.pos.gui.sales.retail.payments.shippingdata;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.validation.ValidationFormGui;

@Component
@Scope("prototype")
public class ShippingDataForm extends ValidationFormGui {

	@NotEmpty(message = "Debe rellenar el campo nombre.")
	@Size(max = 45, message = "La longitud del campo nombre no puede superar los {max} caracteres") 
	protected String name;

	@NotEmpty(message = "Debe rellenar el campo domicilio.")
	@Size(max = 50, message = "La longitud del campo domicilio no puede superar los {max} caracteres")
	protected String address;
	
	@Size(max = 50, message = "La longitud del campo población no puede superar los {max} caracteres")
	protected String city;
	
	@Size(max = 50, message = "La longitud del campo provincia no puede superar los {max} caracteres")
	protected String province;
	
	@Size(max = 50, message = "La longitud del campo localidad no puede superar los {max} caracteres")
	protected String location;
	
	@Size(max = 8, message = "La longitud del campo código postal no puede superar los {max} caracteres")
	protected String postalCode;
	
	//@NotEmpty (message = "El campo num. documento del cliente debe estar relleno.")
	@Size(max = 20, message = "La longitud del campo num. documento no puede superar los {max} caracteres")
	protected String vatNumber;

	@NotEmpty(message = "Debe rellenar el campo telefono.")
	@Size(max = 15, message = "La longitud del campo teléfono no puede superar los {max} caracteres")
	protected String phone;
	

	public void clearForm() {
		name = "";
		address = "";
		phone = "";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
	
	public void setProvince(String province) {
		this.province = province;
	}
	
	public void setLocation(String location) {
		this.location = location;
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
	
	public void setnumDocIdent(String vatNumber) {
		this.vatNumber = vatNumber;
	}
}
