package com.comerzzia.bimbaylola.pos.services.taxFree.objetosXML;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "encryption_method_detail")
@Component
@Primary
@Scope("prototype")
public class EncryptionMethodDetail {

	@XmlElement(name = "encryption_cert_subject")
	protected String encryption_cert_subject;

	public String getEncryption_cert_subject() {
		return encryption_cert_subject;
	}

	public void setEncryption_cert_subject(String encryption_cert_subject) {
		this.encryption_cert_subject = encryption_cert_subject;
	}

}
