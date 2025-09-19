package com.comerzzia.bimbaylola.pos.services.taxFree.objetosXML;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "customer_data")
@Component
@Primary
@Scope("prototype")
public class CustomerData {

	@XmlElement(name = "customer_id")
	protected long customer_id;
	
	@XmlElement(name = "customer_name")
	protected CustomerName customer_name;
	
	@XmlElement(name = "customer_address")
	protected CustomerAddress customer_address;
	
	@XmlElement(name = "zip_code")
	protected int zip_code;
	
	@XmlElement(name = "mobile_no")
	protected String mobile_no;
	
	@XmlElement(name = "DateOfBirth")
	protected Date DateOfBirth;
	
	@XmlElement(name = "passport_number")
	protected String passport_number;
	
	@XmlElement(name = "issued_by_government")
	protected String issued_by_government;
	
	@XmlElement(name = "country_of_origin")
	protected String country_of_origin;
	
	@XmlElement(name = "iso_country_of_origin")
	protected int iso_country_of_origin;
	
	@XmlElement(name = "email")
	protected String email;
	
	@XmlElement(name = "nationality")
	protected int nationality;
	
	@XmlElement(name = "city_of_birth")
	protected String city_of_birth;
	
	@XmlElement(name = "fiscal_code")
	protected String fiscal_code;

	public long getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(long customer_id) {
		this.customer_id = customer_id;
	}

	public CustomerName getCustomer_name() {
		return customer_name;
	}

	public void setCustomer_name(CustomerName customer_name) {
		this.customer_name = customer_name;
	}

	public CustomerAddress getCustomer_address() {
		return customer_address;
	}

	public void setCustomer_address(CustomerAddress customer_address) {
		this.customer_address = customer_address;
	}

	public int getZip_code() {
		return zip_code;
	}

	public void setZip_code(int zip_code) {
		this.zip_code = zip_code;
	}

	public String getMobile_no() {
		return mobile_no;
	}

	public void setMobile_no(String mobile_no) {
		this.mobile_no = mobile_no;
	}

	public Date getDateOfBirth() {
		return DateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		DateOfBirth = dateOfBirth;
	}

	public String getPassport_number() {
		return passport_number;
	}

	public void setPassport_number(String passport_number) {
		this.passport_number = passport_number;
	}

	public String getIssued_by_government() {
		return issued_by_government;
	}

	public void setIssued_by_government(String issued_by_government) {
		this.issued_by_government = issued_by_government;
	}

	public String getCountry_of_origin() {
		return country_of_origin;
	}

	public void setCountry_of_origin(String country_of_origin) {
		this.country_of_origin = country_of_origin;
	}

	public int getIso_country_of_origin() {
		return iso_country_of_origin;
	}

	public void setIso_country_of_origin(int iso_country_of_origin) {
		this.iso_country_of_origin = iso_country_of_origin;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getNationality() {
		return nationality;
	}

	public void setNationality(int nationality) {
		this.nationality = nationality;
	}

	public String getCity_of_birth() {
		return city_of_birth;
	}

	public void setCity_of_birth(String city_of_birth) {
		this.city_of_birth = city_of_birth;
	}

	public String getFiscal_code() {
		return fiscal_code;
	}

	public void setFiscal_code(String fiscal_code) {
		this.fiscal_code = fiscal_code;
	}

}
