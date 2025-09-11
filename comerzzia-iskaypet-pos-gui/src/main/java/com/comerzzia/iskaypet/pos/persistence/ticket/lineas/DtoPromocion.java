package com.comerzzia.iskaypet.pos.persistence.ticket.lineas;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "dto_promocion")
public class DtoPromocion {
	
	private Long itTipoPromocion;
	private Long idPromocion;
	private BigDecimal importeTotalDtoProrrateado;
	private BigDecimal precioSinDto;
	private BigDecimal precioTotalSinDto;
	private boolean accesoCupon;

	public Long getIdPromocion() {
		return idPromocion;
	}

	public void setIdPromocion(Long idPromocion) {
		this.idPromocion = idPromocion;
	}

	public BigDecimal getImporteTotalDtoProrrateado() {
		return importeTotalDtoProrrateado;
	}

	public void setImporteTotalDtoProrrateado(BigDecimal importeTotalDtoProrrateado) {
		this.importeTotalDtoProrrateado = importeTotalDtoProrrateado;
	}

	public Long getItTipoPromocion() {
		return itTipoPromocion;
	}

	public void setItTipoPromocion(Long itTipoPromocion) {
		this.itTipoPromocion = itTipoPromocion;
	}

	public BigDecimal getPrecioSinDto() {
		return precioSinDto;
	}

	public void setPrecioSinDto(BigDecimal precioSinDto) {
		this.precioSinDto = precioSinDto;
	}

	public BigDecimal getPrecioTotalSinDto() {
		return precioTotalSinDto;
	}

	public void setPrecioTotalSinDto(BigDecimal precioTotalSinDto) {
		this.precioTotalSinDto = precioTotalSinDto;
	}
	
	public boolean isAccesoCupon() {
		return accesoCupon;
	}

	public void setAccesoCupon(boolean accesoCupon) {
		this.accesoCupon = accesoCupon;
	}

}
