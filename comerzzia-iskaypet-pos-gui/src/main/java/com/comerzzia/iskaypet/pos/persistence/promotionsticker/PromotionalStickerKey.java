package com.comerzzia.iskaypet.pos.persistence.promotionsticker;

/**
 * GAP62 - PEGATINAS PROMOCIONALES
 */
public class PromotionalStickerKey{

	protected String activityId;
	protected String barcode;

	public String getActivityId(){
		return activityId;
	}

	public void setActivityId(String activityId){
		this.activityId = activityId == null ? null : activityId.trim();
	}

	public String getBarcode(){
		return barcode;
	}

	public void setBarcode(String barcode){
		this.barcode = barcode == null ? null : barcode.trim();
	}
}