package com.comerzzia.iskaypet.pos.persistence.ticket.pagos.sipay;

import java.math.BigDecimal;

public class InfoSipayTransaction {

	protected String codTicket;
	protected BigDecimal amount;
	protected int paymentId;

	public InfoSipayTransaction() {
		super();
	}

	public InfoSipayTransaction(String codTicket, BigDecimal amount, int paymentId) {
		this.codTicket = codTicket;
		this.amount = amount;
		this.paymentId = paymentId;
	}

	public String getCodTicket() {
		return codTicket;
	}

	public void setCodTicket(String codTicket) {
		this.codTicket = codTicket;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public int getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(int paymentId) {
		this.paymentId = paymentId;
	}

}
