package com.comerzzia.bimbaylola.pos.services.taxFree.objetosXML;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "operator_name")
@Component
@Primary
@Scope("prototype")
public class OperatorName {

	@XmlElement(name = "operator_title")
	protected String operator_title;
	
	@XmlElement(name = "operator_first_name")
	protected String operator_first_name;
	
	@XmlElement(name = "operator_last_name")
	protected String operator_last_name;

	public String getOperator_title() {
		return operator_title;
	}

	public void setOperator_title(String operator_title) {
		this.operator_title = operator_title;
	}

	public String getOperator_first_name() {
		return operator_first_name;
	}

	public void setOperator_first_name(String operator_first_name) {
		this.operator_first_name = operator_first_name;
	}

	public String getOperator_last_name() {
		return operator_last_name;
	}

	public void setOperator_last_name(String operator_last_name) {
		this.operator_last_name = operator_last_name;
	}

}
