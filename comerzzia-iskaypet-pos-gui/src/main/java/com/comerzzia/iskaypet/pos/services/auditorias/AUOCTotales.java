package com.comerzzia.iskaypet.pos.services.auditorias;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

//GAP 113: AMPLIACIÓN DESARROLLO AUDITORÍAS EN POS
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "totales")
public class AUOCTotales {

	private BigDecimal base;
	private BigDecimal total;

	public BigDecimal getBase() {
		return base;
	}

	public void setBase(BigDecimal base) {
		this.base = base;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

}
