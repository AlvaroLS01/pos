package com.comerzzia.bimbaylola.pos.services.ticket;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "impuesto_linea")
public class ImpuestoLinea {

	@XmlElement(name = "codigo_impuesto")
	protected String codImp;

	@XmlElement(name = "porcentaje")
	protected BigDecimal porcentaje;

	@XmlElement(name = "porcentaje_recargo")
	protected BigDecimal porcentajeRecargo;

	public String getCodImp() {
		return codImp;
	}

	public void setCodImp(String codImp) {
		this.codImp = codImp;
	}

	public BigDecimal getPorcentaje() {
		return porcentaje;
	}

	public void setPorcentaje(BigDecimal porcentaje) {
		this.porcentaje = porcentaje;
	}

	public BigDecimal getPorcentajeRecargo() {
		return porcentajeRecargo;
	}

	public void setPorcentajeRecargo(BigDecimal porcentajeRecargo) {
		this.porcentajeRecargo = porcentajeRecargo;
	}

}
