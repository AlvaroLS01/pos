package com.comerzzia.bimbaylola.pos.services.taxFree.objetosXML;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "customer_name")
@Component
@Primary
@Scope("prototype")
public class CustomerName {

	@XmlElement(name = "customer_title")
	protected String customer_title;
	
	@XmlElement(name = "customer_first_name")
	protected String customer_first_name;
	
	@XmlElement(name = "customer_last_name")
	protected String customer_last_name;

	public String getCustomer_title() {
		return customer_title;
	}

	public void setCustomer_title(String customer_title) {
		this.customer_title = customer_title;
	}

	public String getCustomer_first_name() {
		return customer_first_name;
	}

	public void setCustomer_first_name(String customer_first_name) {
		this.customer_first_name = customer_first_name;
	}

	public String getCustomer_last_name() {
		return customer_last_name;
	}

	public void setCustomer_last_name(String customer_last_name) {
		this.customer_last_name = customer_last_name;
	}

}
