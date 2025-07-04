package com.comerzzia.cardoso.pos.services.ticket.cabecera.adicionales;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * GAP - PERSONALIZACIONES V3 - PROMOCIONES MONOGRÁFICAS
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "promocion")
public class DatosDescuentoMonograficaBean{

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

}
