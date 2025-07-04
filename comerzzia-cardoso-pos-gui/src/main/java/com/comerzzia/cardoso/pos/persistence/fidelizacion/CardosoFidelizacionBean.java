package com.comerzzia.cardoso.pos.persistence.fidelizacion;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlTransient;

import com.comerzzia.pos.persistence.fidelizacion.FidelizacionBean;

public class CardosoFidelizacionBean extends FidelizacionBean {

	@XmlTransient
	private BigDecimal importeDescuentoAcumulado;

	public BigDecimal getImporteDescuentoAcumulado() {
		return importeDescuentoAcumulado;
	}

	public void setImporteDescuentoAcumulado(BigDecimal importeDescuentoAcumulado) {
		this.importeDescuentoAcumulado = importeDescuentoAcumulado;
	}

}
