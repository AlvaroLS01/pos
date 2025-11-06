package com.comerzzia.dinosol.pos.services.promociones.opciones;

public class PromocionOpcionDto {

	private String idPromocionSap;

	private Long idPromocionCzz;

	private String codigoAcceso;

	public String getIdPromocionSap() {
		return idPromocionSap;
	}

	public void setIdPromocionSap(String idPromocionSap) {
		this.idPromocionSap = idPromocionSap;
	}

	public Long getIdPromocionCzz() {
		return idPromocionCzz;
	}

	public void setIdPromocionCzz(Long idPromocionCzz) {
		this.idPromocionCzz = idPromocionCzz;
	}

	public String getCodigoAcceso() {
		return codigoAcceso;
	}

	public void setCodigoAcceso(String codigoAcceso) {
		this.codigoAcceso = codigoAcceso;
	}

	@Override
	public String toString() {
		return "(SAP: " + idPromocionSap + ", codigo acceso=" + codigoAcceso + ")";
	}

}
