package com.comerzzia.pos.gui.sales.loyalcustomer;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.validation.ValidationFormGui;

@Component
@Scope("prototype")
public class LoyalCustomerFormValidationBean extends ValidationFormGui{
	
	
	@Size(max=30, message="La longitud del campo Código no puede superar los {max} caracteres.")
	protected String code;
	
	protected String cardNumber;
	
	@NotEmpty(message="Debe rellenar el campo nombre")
	@Size(max=45, message="La longitud del campo Nombre no puede superar los {max} caracteres.")
	protected String name;
	
	@NotEmpty(message="Debe rellenar el campo apellidos")
	@Size(max=45, message="La longitud del campo Apellidos no puede superar los {max} caracteres.")
	protected String lastName;
	
	@NotNull(message="Debe seleccionar un tipo de documento")
	@NotEmpty(message="Debe seleccionar un tipo de documento")
	protected String documentType;
	@NotEmpty(message="Debe rellenar el campo documento")
	@Size(max=20, message="La longitud del campo Documento no puede superar los {max} caracteres.")
	protected String document;
	
	protected String genre;
	
	protected String civilStatus;
	
	protected Date birthDate;
	
	@NotEmpty(message="Debe rellenar el campo email")
	@Size(max=255, message="La longitud del campo Email no puede superar los {max} caracteres.")
	protected String email;
	
	@Size(max=255, message="La longitud del campo Móvil no puede superar los {max} caracteres.")
	protected String mobile;
	
	
	@Size(max=4, message="La longitud del campo Código de Pais no puede superar los {max} caracteres.")
	protected String countryCode;
	protected String countryDes;
	
	@Size(max=8, message="La longitud del campo Código Postal no puede superar los {max} caracteres.")
	protected String postalCode;
	
	@NotEmpty(message="Debe rellenar el campo provincia")
	@Size(max=50, message="La longitud del campo Provincia no puede superar los {max} caracteres.")
	protected String province;
	
	@NotEmpty(message="Debe rellenar el campo población")
	@Size(max=50, message="La longitud del campo Población no puede superar los {max} caracteres.")
	protected String city;
	
	@Size(max=50, message="La Longitud del campo Localidad no puede superar los {max} caracteres.")
	protected String location;
	
	@NotEmpty(message="Debe rellenar el campo domicilio")
	@Size(max=50, message="La longitud del campo Domicilio no puede superar los {max} caracteres.")
	protected String address;
	
	@Size(max=255, message = "La longitud del campo Observaciones no puede superar los {max} caracteres.")
	protected String remarks;
	
	protected String favStoreCode;
	protected String favStoreDes;
	
	protected String collectiveCode;
	protected String collectiveDes;

	@Override
	public void clearForm() {
		code = "";
		cardNumber = "";
		name = "";
		lastName = "";
		documentType = null;
		document = "";
		birthDate = null;
		genre = null;
		civilStatus = null;
		email = "";
		mobile = "";
		countryCode = "";
		countryDes = "";
		postalCode = "";
		province = "";
		city = "";
		location = "";
		address = "";
		favStoreCode = "";
		favStoreDes = "";
		collectiveCode = "";
		collectiveDes = "";
		remarks = "";
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public String getDocument() {
		return document;
	}

	public void setDocument(String document) {
		this.document = document;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String getCivilStatus() {
		return civilStatus;
	}

	public void setCivilStatus(String civilStatus) {
		this.civilStatus = civilStatus;
	}

	public Date getFechaNacimiento() {
		return birthDate;
	}

	public void setFechaNacimiento(Date birthDate) {
		this.birthDate = birthDate;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getCountryDes() {
		return countryDes;
	}

	public void setCountryDes(String countryDes) {
		this.countryDes = countryDes;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
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
	
	public String getLocation(){
		return location;
	}
	
	public void setLocation(String location){
		this.location = location;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getFavStoreCode() {
		return favStoreCode;
	}

	public void setFavStoreCode(String favStoreCode) {
		this.favStoreCode = favStoreCode;
	}

	public String getFavStoreDes() {
		return favStoreDes;
	}

	public void setFavStoreDes(String favStoreDes) {
		this.favStoreDes = favStoreDes;
	}

	public String getCollectiveCode() {
		return collectiveCode;
	}

	public void setCollectiveCode(String collectiveCode) {
		this.collectiveCode = collectiveCode;
	}

	public String getCollectiveDes() {
		return collectiveDes;
	}

	public void setCollectiveDes(String collectiveDes) {
		this.collectiveDes = collectiveDes;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

}
