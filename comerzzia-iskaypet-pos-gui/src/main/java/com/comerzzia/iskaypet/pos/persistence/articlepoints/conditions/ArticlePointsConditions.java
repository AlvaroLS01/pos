package com.comerzzia.iskaypet.pos.persistence.articlepoints.conditions;

import java.math.BigDecimal;

/**
 * GAP46 - CANJEO ARTÍCULOS POR PUNTOS
 */
public class ArticlePointsConditions extends ArticlePointsConditionsKey{

	protected BigDecimal quantity;

	public BigDecimal getQuantity(){
		return quantity;
	}

	public void setQuantity(BigDecimal quantity){
		this.quantity = quantity;
	}
	
}