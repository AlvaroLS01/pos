package com.comerzzia.iskaypet.pos.persistence.articlepoints;

/**
 * GAP46 - CANJEO ARTÍCULOS POR PUNTOS
 */
public class ArticlePointsBeanKey{

	protected String activityId;

	protected String itemcode;

	public String getActivityId(){
		return activityId;
	}

	public void setActivityId(String activityId){
		this.activityId = activityId == null ? null : activityId.trim();
	}

	public String getItemcode(){
		return itemcode;
	}

	public void setItemcode(String itemcode){
		this.itemcode = itemcode == null ? null : itemcode.trim();
	}
}