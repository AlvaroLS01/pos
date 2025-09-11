package com.comerzzia.iskaypet.pos.persistence.tickets.articulos.promociones.tipos.combinado;

public class IskaypetArticulosPromo {

	private int idPromocion;
	private String descripcion;
	private String codart;
	private String codartCondition;
	private Double precioPack;
	private String textoPromocion;
	private String promoFidelizado;

	public IskaypetArticulosPromo() {
	}

	public IskaypetArticulosPromo(int idPromocion, String descripcion, String codart, String datosPromocion) {
		this.idPromocion = idPromocion;
		this.descripcion = descripcion;
		this.codart = codart;
	}

	public int getIdPromocion() {
		return idPromocion;
	}

	public void setIdPromocion(int id) {
		this.idPromocion = id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getCodart() {
		return codart;
	}

	public void setCodart(String codart) {
		this.codart = codart;
	}

	public Double getPrecioPack() {
		return precioPack;
	}

	public void setPrecioPack(Double precioPack) {
		this.precioPack = precioPack;
	}

	public String getTextoPromocion() {
		return textoPromocion;
	}

	public void setTextoPromocion(String textoPromocion) {
		this.textoPromocion = textoPromocion;
	}

	public String getPromoFidelizado() {
		return promoFidelizado;
	}

	public void setPromoFidelizado(String promoFidelizado) {
		this.promoFidelizado = promoFidelizado;
	}

	public String getCodartCondition() {
		return codartCondition;
	}

	public void setCodartCondition(String codartCondition) {
		this.codartCondition = codartCondition;
	}

}
