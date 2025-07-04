package com.comerzzia.cardoso.pos.services.ticket.cabecera.adicionales;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "datos_descuento_gea")
public class DatosDescuentoGea {

	@XmlElement(name = "porcentajeDescuento")
	protected BigDecimal porcentajeDescuentoSobreTotalVenta;

	@XmlElement(name = "importeDescuentoLinea")
	protected BigDecimal importeDescuentoLinea;

	public DatosDescuentoGea() {
		porcentajeDescuentoSobreTotalVenta = BigDecimal.ZERO;
		importeDescuentoLinea = BigDecimal.ZERO;
	}

	public BigDecimal getPorcentajeDescuento() {
		return porcentajeDescuentoSobreTotalVenta;
	}

	public void setPorcentajeDescuento(BigDecimal porcentajeDescuento) {
		this.porcentajeDescuentoSobreTotalVenta = porcentajeDescuento;
	}

	public BigDecimal getImporteDescuentoLinea() {
		return importeDescuentoLinea;
	}

	public void setImporteDescuentoLinea(BigDecimal importeDescuentoLinea) {
		this.importeDescuentoLinea = importeDescuentoLinea;
	}

}
