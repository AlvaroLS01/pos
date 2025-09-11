package com.comerzzia.iskaypet.pos.persistence.promotionsticker;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.springframework.stereotype.Component;

/**
 * GAP62 - PEGATINAS PROMOCIONALES
 */
@Component
@XmlRootElement(name = "pegatina_promocion")
@XmlAccessorType(XmlAccessType.NONE)
public class PromotionalStickerXML{

	@XmlElement(name = "ean")
	protected String ean;
	@XmlElement(name = "descuento")
	protected BigDecimal discount;
	@XmlElement(name = "motivo")
	protected Long motived;
	
	// Variable que solo usamos a la hora de insertar para subir el dato.
	@XmlTransient
	protected String codArticle;

	public String getEan(){
		return ean;
	}

	public void setEan(String ean){
		this.ean = ean;
	}

	public BigDecimal getDiscount(){
		return discount;
	}

	public void setDiscount(BigDecimal discount){
		this.discount = discount;
	}
	
	public Long getMotived(){
		return motived;
	}
	
	public void setMotived(Long motived){
		this.motived = motived;
	}

	public String getCodArticle(){
		return codArticle;
	}
	
	public void setCodArticle(String codArticle){
		this.codArticle = codArticle;
	}
	
}
