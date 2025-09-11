package com.comerzzia.iskaypet.pos.persistence.ticket.lineas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="textoPromocion")
public class TextoPromocion {
	
	protected Long idPromocion;
	protected String texto;
	
	public Long getIdPromocion() {
		return idPromocion;
	}
	public void setIdPromocion(Long idPromocion) {
		this.idPromocion = idPromocion;
	}
	public String getTexto() {
		return texto;
	}
	public void setTexto(String texto) {
		this.texto = texto;
	}

}
