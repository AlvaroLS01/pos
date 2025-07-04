package com.comerzzia.cardoso.pos.services.ticket.lineas;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * GAP - PERSONALIZACIONES V3 - LOTES
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class CardosoLote{

	@XmlElement(name = "id")
	private String id;
	@XmlElement(name = "cantidad")
	private BigDecimal cantidad;

	public String getId(){
		return id;
	}

	public void setId(String id){
		this.id = id;
	}

	public BigDecimal getCantidad(){
		return cantidad;
	}

	public void setCantidad(BigDecimal cantidad){
		this.cantidad = cantidad;
	}

	@Override
	public boolean equals(Object obj){
		return obj instanceof CardosoLote && id.equals(((CardosoLote) obj).getId());
	}

}
