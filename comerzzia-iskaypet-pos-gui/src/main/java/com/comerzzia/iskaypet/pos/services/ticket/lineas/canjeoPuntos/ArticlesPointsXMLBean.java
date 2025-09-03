package com.comerzzia.iskaypet.pos.services.ticket.lineas.canjeoPuntos;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * GAP46 - CANJEO ARTÍCULOS POR PUNTOS
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "canjeo_puntos")
public class ArticlesPointsXMLBean{

	public static final String VALUE_REEDEM_OK = "1";
	public static final String VALUE_REEDEM_KO = "0";

	@XmlElement(name = "puntos")
	protected BigDecimal points;
	@XmlElement(name = "valor")
	protected BigDecimal value;
	@XmlElement(name = "canjeado")
	protected String reedem;

	public ArticlesPointsXMLBean(){
		super();
	}
	
	public ArticlesPointsXMLBean(BigDecimal points, BigDecimal value, String reedem){
		super();
		this.points = points;
		this.value = value;
		this.reedem = reedem;
	}

	public BigDecimal getPoints(){
		return points;
	}

	public void setPoints(BigDecimal points){
		this.points = points;
	}

	public BigDecimal getValue(){
		return value;
	}

	public void setValue(BigDecimal value){
		this.value = value;
	}

	public String getReedem(){
		return reedem;
	}

	public void setReedem(String reedem){
		this.reedem = reedem;
	}

}
