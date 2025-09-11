package com.comerzzia.iskaypet.pos.persistence.ticket.comprasfidelizado;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "SolicitudComprasFidelizadoResponse")
public class SolicitudComprasFidelizadoResponse {
	
	private List<ComprasFidelizado> comprasFidelizado;

	public List<ComprasFidelizado> getComprasFidelizado() {
		return comprasFidelizado;
	}
	
	public void setComprasFidelizado(List<ComprasFidelizado> comprasFidelizado) {
		this.comprasFidelizado = comprasFidelizado;
	}

}
