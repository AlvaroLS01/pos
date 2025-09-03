package com.comerzzia.iskaypet.pos.persistence.articlepoints.conditions;

/**
 * GAP46 - CANJEO ARTÍCULOS POR PUNTOS
 */
public class ArticlePointsConditionsKey{

	protected String activityId;

	protected String itemcode;

	protected String itemcodeConditional;

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

	public String getItemcodeConditional(){
		return itemcodeConditional;
	}

	public void setItemcodeConditional(String itemcodeConditional){
		this.itemcodeConditional = itemcodeConditional == null ? null : itemcodeConditional.trim();
	}
}