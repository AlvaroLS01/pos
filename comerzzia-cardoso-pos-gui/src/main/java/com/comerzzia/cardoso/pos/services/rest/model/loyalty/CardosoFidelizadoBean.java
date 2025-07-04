package com.comerzzia.cardoso.pos.services.rest.model.loyalty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.comerzzia.api.model.loyalty.FidelizadoBean;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="fidelizado")
@XmlType(name="fid", namespace = "cardoso")
public class CardosoFidelizadoBean extends FidelizadoBean {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8534892138279338980L;
	
	protected String importeDescuentoAcumulado;

	public String getImporteDescuentoAcumulado() {
		return importeDescuentoAcumulado;
	}

	public void setImporteDescuentoAcumulado(String importeDescuentoAcumulado) {
		this.importeDescuentoAcumulado = importeDescuentoAcumulado;
	}
}
