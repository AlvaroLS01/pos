package com.comerzzia.bimbaylola.pos.services.taxFree.objetosXML;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "terminal")
@Component
@Primary
@Scope("prototype")
public class Terminal {

	@XmlElement(name = "terminal_id")
	protected int terminal_id;
	
	@XmlElement(name = "training_mode")
	protected int training_mode;

	public int getTerminal_id() {
		return terminal_id;
	}

	public void setTerminal_id(int terminal_id) {
		this.terminal_id = terminal_id;
	}

	public int getTraining_mode() {
		return training_mode;
	}

	public void setTraining_mode(int training_mode) {
		this.training_mode = training_mode;
	}

}