package com.comerzzia.iskaypet.pos.persistence.articlepoints;

import java.math.BigDecimal;

/**
 * GAP46 - CANJEO ARTÍCULOS POR PUNTOS
 */
public class ArticlePointsBean extends ArticlePointsBeanKey{

	protected BigDecimal points;

	protected BigDecimal price;

	public BigDecimal getPoints(){
		return points;
	}

	public void setPoints(BigDecimal points){
		this.points = points;
	}

	public BigDecimal getPrice(){
		return price;
	}

	public void setPrice(BigDecimal price){
		this.price = price;
	}
}