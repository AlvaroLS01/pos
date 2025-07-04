package com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas.refund.request;

public class RequestDTO {

	private String urlHost;
	private String puerto;
	private String globalMerchants;
	private String pos;
	private String payments;
	private Integer amount;
	private String comment;

	public String getUrlHost() {
		return urlHost;
	}

	public void setUrlHost(String urlHost) {
		this.urlHost = urlHost;
	}

	public String getPuerto() {
		return puerto;
	}

	public void setPuerto(String puerto) {
		this.puerto = puerto;
	}

	public String getGlobalMerchants() {
		return globalMerchants;
	}

	public void setGlobalMerchants(String globalMerchants) {
		this.globalMerchants = globalMerchants;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getPayments() {
		return payments;
	}

	public void setPayments(String payments) {
		this.payments = payments;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}
