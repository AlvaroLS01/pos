package com.comerzzia.iskaypet.pos.persistence.closingday;

/**
 * GAP27.2 - AMPLIACIÓN DEL CIERRE DE FIN DE DÍA
 */
public class HeaderClosingEndDayKey{

	private String activityUid;
	private String endDayUid;

	public String getActivityUid(){
		return activityUid;
	}

	public void setActivityUid(String activityUid){
		this.activityUid = activityUid == null ? null : activityUid.trim();
	}

	public String getEndDayUid(){
		return endDayUid;
	}

	public void setEndDayUid(String endDayUid){
		this.endDayUid = endDayUid == null ? null : endDayUid.trim();
	}
}