package com.comerzzia.cardoso.pos.persistence.auditorias;

public class AuditoriaDTO {

	private Integer comercio;
	private String importe;
	private String observaciones;
	private Long paymentid;
	private Integer posId;
	private String tipoOperacion;
	private Long paymentIdOrigen;

	public Long getPaymentIdOrigen() {
		return paymentIdOrigen;
	}

	public void setPaymentIdOrigen(Long paymentIdOrigen) {
		this.paymentIdOrigen = paymentIdOrigen;
	}

	public int getComercio() {
		return comercio;
	}

	public void setComercio(Integer comercio) {
		this.comercio = comercio;
	}

	public String getImporte() {
		return importe;
	}

	public void setImporte(String importe) {
		this.importe = importe;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public Long getPaymentid() {
		return paymentid;
	}

	public void setPaymentid(Long paymentid) {
		this.paymentid = paymentid;
	}

	public int getPosId() {
		return posId;
	}

	public void setPosId(Integer posId) {
		this.posId = posId;
	}

	public String getTipoOperacion() {
		return tipoOperacion;
	}

	public void setTipoOperacion(String tipoOperacion) {
		this.tipoOperacion = tipoOperacion;
	}

}
