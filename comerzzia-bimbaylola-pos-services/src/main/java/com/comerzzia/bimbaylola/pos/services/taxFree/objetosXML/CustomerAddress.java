package com.comerzzia.bimbaylola.pos.services.taxFree.objetosXML;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "customer_address")
@Component
@Primary
@Scope("prototype")
public class CustomerAddress {

	@XmlElement(name = "customer_address_line_one")
	protected String customer_address_line_one;
	
	@XmlElement(name = "customer_address_line_two")
	protected String customer_address_line_two;
	
	@XmlElement(name = "customer_address_line_three")
	protected String customer_address_line_three;
	
	@XmlElement(name = "customer_address_line_four")
	protected int customer_address_line_four;
	
	@XmlElement(name = "customer_address_line_five")
	protected String customer_address_line_five;

	public String getCustomer_address_line_one() {
		return customer_address_line_one;
	}

	public void setCustomer_address_line_one(String customer_address_line_one) {
		this.customer_address_line_one = customer_address_line_one;
	}

	public String getCustomer_address_line_two() {
		return customer_address_line_two;
	}

	public void setCustomer_address_line_two(String customer_address_line_two) {
		this.customer_address_line_two = customer_address_line_two;
	}

	public String getCustomer_address_line_three() {
		return customer_address_line_three;
	}

	public void setCustomer_address_line_three(String customer_address_line_three) {
		this.customer_address_line_three = customer_address_line_three;
	}

	public int getCustomer_address_line_four() {
		return customer_address_line_four;
	}

	public void setCustomer_address_line_four(int customer_address_line_four) {
		this.customer_address_line_four = customer_address_line_four;
	}

	public String getCustomer_address_line_five() {
		return customer_address_line_five;
	}

	public void setCustomer_address_line_five(String customer_address_line_five) {
		this.customer_address_line_five = customer_address_line_five;
	}

}
