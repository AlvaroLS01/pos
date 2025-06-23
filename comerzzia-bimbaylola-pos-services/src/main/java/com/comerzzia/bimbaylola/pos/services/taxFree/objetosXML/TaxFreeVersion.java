package com.comerzzia.bimbaylola.pos.services.taxFree.objetosXML;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "version")
@Component
@Primary
@Scope("prototype")
public class TaxFreeVersion {

	@XmlElement(name = "xml_version")
	protected String xml_version;
	
	@XmlElement(name = "pos_application_version")
	protected String pos_application_version;
	
	@XmlElement(name = "client_version")
	protected String client_version;

	public String getXml_version() {
		return xml_version;
	}

	public void setXml_version(String xml_version) {
		this.xml_version = xml_version;
	}

	public String getPos_application_version() {
		return pos_application_version;
	}

	public void setPos_application_version(String pos_application_version) {
		this.pos_application_version = pos_application_version;
	}

	public String getClient_version() {
		return client_version;
	}

	public void setClient_version(String client_version) {
		this.client_version = client_version;
	}

}
