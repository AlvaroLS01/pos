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
@XmlRootElement(name = "transaction_totals")
@Component
@Primary
@Scope("prototype")
public class TransactionTotals {

	@XmlElement(name = "transaction_net_amount")
	protected BigDecimal transaction_net_amount;

	@XmlElement(name = "transaction_gross_amount")
	protected BigDecimal transaction_gross_amount;

	@XmlElement(name = "transaction_vat_amount")
	protected BigDecimal transaction_vat_amount;

	public BigDecimal getTransaction_net_amount() {
		return transaction_net_amount;
	}

	public void setTransaction_net_amount(BigDecimal transaction_net_amount) {
		this.transaction_net_amount = transaction_net_amount;
	}

	public BigDecimal getTransaction_gross_amount() {
		return transaction_gross_amount;
	}

	public void setTransaction_gross_amount(BigDecimal transaction_gross_amount) {
		this.transaction_gross_amount = transaction_gross_amount;
	}

	public BigDecimal getTransaction_vat_amount() {
		return transaction_vat_amount;
	}

	public void setTransaction_vat_amount(BigDecimal transaction_vat_amount) {
		this.transaction_vat_amount = transaction_vat_amount;
	}

}
