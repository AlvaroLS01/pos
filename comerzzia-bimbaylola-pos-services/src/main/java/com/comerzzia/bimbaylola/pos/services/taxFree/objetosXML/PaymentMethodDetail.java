package com.comerzzia.bimbaylola.pos.services.taxFree.objetosXML;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "payment_metho_detail")
@Component
@Primary
@Scope("prototype")
public class PaymentMethodDetail {

	@XmlElement(name = "payment_method")
	protected int payment_method;
	
	@XmlElement(name = "amount")
	protected BigDecimal amount;

	public int getPayment_method() {
		return payment_method;
	}

	public void setPayment_method(int payment_method) {
		this.payment_method = payment_method;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

}
