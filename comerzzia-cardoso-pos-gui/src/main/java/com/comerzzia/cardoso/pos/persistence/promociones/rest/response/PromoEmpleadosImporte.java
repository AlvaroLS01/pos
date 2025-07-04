package com.comerzzia.cardoso.pos.persistence.promociones.rest.response;

import java.math.BigDecimal;

/**
 * GAP - PERSONALIZACIONES V3 - PROMOCIÓN EMPLEADO
 */
public class PromoEmpleadosImporte {

	private String numeroTarjeta;
	private Long idPromocion;
	private BigDecimal importe;

	public PromoEmpleadosImporte(String numeroTarjeta, Long idPromocion, BigDecimal importe){
		this.numeroTarjeta = numeroTarjeta;
		this.idPromocion = idPromocion;
		this.importe = importe;
	}

	public PromoEmpleadosImporte(){}

	public String getNumeroTarjeta(){
		return numeroTarjeta;
	}

	public void setNumeroTarjeta(String numeroTarjeta){
		this.numeroTarjeta = numeroTarjeta;
	}

	public Long getIdPromocion(){
		return idPromocion;
	}

	public void setIdPromocion(Long idPromocion){
		this.idPromocion = idPromocion;
	}

	public BigDecimal getImporte(){
		return importe;
	}

	public void setImporte(BigDecimal importe){
		this.importe = importe;
	}

}
