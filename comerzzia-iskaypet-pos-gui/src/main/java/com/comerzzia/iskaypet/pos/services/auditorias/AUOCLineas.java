package com.comerzzia.iskaypet.pos.services.auditorias;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "linea")
public class AUOCLineas {
	@XmlElement(name = "codart")
	private String codArt;
	@XmlElement(name = "desart")
	private String desArt;
	@XmlElement(name = "cantidad")
	private BigDecimal cantidad;
	@XmlElement(name = "desglose1")
	private String desglose1;
	@XmlElement(name = "desglose2")
	private String desglose2;
	@XmlElement(name = "precio_tarifa_origen")
	private BigDecimal pretioTarifaOrigen;
	@XmlElement(name = "precio_total_tarifa_origen")
	private BigDecimal precioTotalTarifaOrigen;
	@XmlElement(name = "precio_sin_dto")
	private BigDecimal precioSinDto;
	@XmlElement(name = "precio_total_sin_dto")
	private BigDecimal precioTotalSinDto;
	@XmlElement(name = "precio")
	private BigDecimal precio;
	@XmlElement(name = "precio_total")
	private BigDecimal precioTotal;
	@XmlElement(name = "importe")
	private BigDecimal importe;
	@XmlElement(name = "importe_total")
	private BigDecimal importeTotal;

	public String getCodArt() {
		return codArt;
	}

	public void setCodArt(String codArt) {
		this.codArt = codArt;
	}

	public String getDesArt() {
		return desArt;
	}

	public void setDesArt(String desArt) {
		this.desArt = desArt;
	}

	public BigDecimal getCantidad() {
		return cantidad;
	}

	public void setCantidad(BigDecimal cantidad) {
		this.cantidad = cantidad;
	}

	public String getDesglose1() {
		return desglose1;
	}

	public void setDesglose1(String desglose1) {
		this.desglose1 = desglose1;
	}

	public String getDesglose2() {
		return desglose2;
	}

	public void setDesglose2(String desglose2) {
		this.desglose2 = desglose2;
	}

	public BigDecimal getPretioTarifaOrigen() {
		return pretioTarifaOrigen;
	}

	public void setPretioTarifaOrigen(BigDecimal pretioTarifaOrigen) {
		this.pretioTarifaOrigen = pretioTarifaOrigen;
	}

	public BigDecimal getPrecioTotalTarifaOrigen() {
		return precioTotalTarifaOrigen;
	}

	public void setPrecioTotalTarifaOrigen(BigDecimal precioTotalTarifaOrigen) {
		this.precioTotalTarifaOrigen = precioTotalTarifaOrigen;
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

	public BigDecimal getPrecio() {
		return precio;
	}

	public void setPrecio(BigDecimal precio) {
		this.precio = precio;
	}

	public BigDecimal getImporte() {
		return importe;
	}

	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}

	public BigDecimal getImporteTotal() {
		return importeTotal;
	}

	public void setImporteTotal(BigDecimal importeTotal) {
		this.importeTotal = importeTotal;
	}

	public BigDecimal getPrecioTotal() {
		return precioTotal;
	}

	public void setPrecioTotal(BigDecimal precioTotal) {
		this.precioTotal = precioTotal;
	}

	
}
