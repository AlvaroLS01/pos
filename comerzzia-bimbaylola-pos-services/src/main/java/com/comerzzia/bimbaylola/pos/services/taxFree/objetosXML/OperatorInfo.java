package com.comerzzia.bimbaylola.pos.services.taxFree.objetosXML;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "operator_info")
@Component
@Primary
@Scope("prototype")
public class OperatorInfo {

	@XmlElement(name = "operator_id")
	protected int operator_id;
	
	@XmlElement(name = "operator_name")
	protected OperatorName operator_name;

	public int getOperator_id() {
		return operator_id;
	}

	public void setOperator_id(int operator_id) {
		this.operator_id = operator_id;
	}

	public OperatorName getOperator_name() {
		return operator_name;
	}

	public void setOperator_name(OperatorName operator_name) {
		this.operator_name = operator_name;
	}

}
