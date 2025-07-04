package com.comerzzia.cardoso.pos.persistence.promociones;

import java.math.BigDecimal;
import java.util.Date;

/**
 * GAP - PROMOCION CANDIDATA
 */
public class PromocionCandidataBean{

	private Long idPromocion;

	private String codtar;

	private Long idTipoPromocion;

	private Date fechaInicio;

	private Date fechaFin;

	private String codArt;

	private BigDecimal precioTarifa;

	public Long getIdPromocion(){
		return idPromocion;
	}

	public void setIdPromocion(Long idPromocion){
		this.idPromocion = idPromocion;
	}

	public String getCodtar(){
		return codtar;
	}

	public void setCodtar(String codtar){
		this.codtar = codtar;
	}

	public Long getIdTipoPromocion(){
		return idTipoPromocion;
	}

	public void setIdTipoPromocion(Long idTipoPromocion){
		this.idTipoPromocion = idTipoPromocion;
	}

	public Date getFechaInicio(){
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio){
		this.fechaInicio = fechaInicio;
	}

	public Date getFechaFin(){
		return fechaFin;
	}

	public void setFechaFin(Date fechaFin){
		this.fechaFin = fechaFin;
	}

	public String getCodArt(){
		return codArt;
	}

	public void setCodArt(String codArt){
		this.codArt = codArt;
	}

	public BigDecimal getPrecioTarifa(){
		return precioTarifa;
	}

	public void setPrecioTarifa(BigDecimal precioTarifa){
		this.precioTarifa = precioTarifa;
	}
}
