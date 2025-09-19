package com.comerzzia.bimbaylola.pos.services.taxFree.objetosXML;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "merchant_data")
@Component
@Primary
@Scope("prototype")
public class MerchantData {

	@XmlElement(name = "merchant_vat_number")
	protected String merchant_vat_number;
	
	@XmlElement(name = "merchant_country_code")
	protected int merchant_country_code;
	
	@XmlElement(name = "merchant_id")
	protected int merchant_id;

	public String getMerchant_vat_number() {
		return merchant_vat_number;
	}

	public void setMerchant_vat_number(String merchant_vat_number) {
		this.merchant_vat_number = merchant_vat_number;
	}

	public int getMerchant_country_code() {
		return merchant_country_code;
	}

	public void setMerchant_country_code(int merchant_country_code) {
		this.merchant_country_code = merchant_country_code;
	}

	public int getMerchant_id() {
		return merchant_id;
	}

	public void setMerchant_id(int merchant_id) {
		this.merchant_id = merchant_id;
	}

}
