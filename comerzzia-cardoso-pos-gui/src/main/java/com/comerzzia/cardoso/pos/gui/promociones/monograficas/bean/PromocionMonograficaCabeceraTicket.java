package com.comerzzia.cardoso.pos.gui.promociones.monograficas.bean;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * GAP - PERSONALIZACIONES V3 - PROMOCIONES MONOGRÁFICAS
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "descuento_monografica")
public class PromocionMonograficaCabeceraTicket{

	@XmlElement(name = "importe")
	private BigDecimal importe;

	@XmlElement(name = "importeTotal")
	private BigDecimal importeTotal;

	public BigDecimal getImporte(){
		return importe;
	}

	public void setImporte(BigDecimal importe){
		this.importe = importe;
	}

	public BigDecimal getImporteTotal(){
		return importeTotal;
	}

	public void setImporteTotal(BigDecimal importeTotal){
		this.importeTotal = importeTotal;
	}
	
	public BigDecimal getSumaImportes(){
		this.importe = (this.importe == null) ? BigDecimal.ZERO : this.importe;
		this.importeTotal = (this.importeTotal == null) ? BigDecimal.ZERO : this.importeTotal;
		return importe.add(importeTotal);
	}
	
}
