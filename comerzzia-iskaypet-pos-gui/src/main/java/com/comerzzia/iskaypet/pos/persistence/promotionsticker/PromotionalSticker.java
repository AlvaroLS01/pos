package com.comerzzia.iskaypet.pos.persistence.promotionsticker;

import java.math.BigDecimal;
import java.util.Date;

/**
 * GAP62 - PEGATINAS PROMOCIONALES
 */
public class PromotionalSticker extends PromotionalStickerKey{

	protected Date creationDate;
	protected String itemCode;
	protected String itemDes;
	protected BigDecimal discount;
	protected Long reasonCode;
	protected String available;

	public Date getCreationDate(){
		return creationDate;
	}

	public void setCreationDate(Date creationDate){
		this.creationDate = creationDate;
	}

	public String getItemCode(){
		return itemCode;
	}

	public void setItemCode(String itemCode){
		this.itemCode = itemCode == null ? null : itemCode.trim();
	}

	public String getItemDes(){
		return itemDes;
	}

	public void setItemDes(String itemDes){
		this.itemDes = itemDes == null ? null : itemDes.trim();
	}

	public BigDecimal getDiscount(){
		return discount;
	}

	public void setDiscount(BigDecimal discount){
		this.discount = discount;
	}

	public Long getReasonCode(){
		return reasonCode;
	}

	public void setReasonCode(Long reasonCode){
		this.reasonCode = reasonCode;
	}

	public String getAvailable(){
		return available;
	}

	public void setAvailable(String available){
		this.available = available == null ? null : available.trim();
	}
}