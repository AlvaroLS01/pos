package com.comerzzia.iskaypet.pos.persistence.closingday;

import java.math.BigDecimal;
import java.util.Date;

/**
 * GAP27.2 - AMPLIACIÓN DEL CIERRE DE FIN DE DÍA
 */
public class HeaderClosingEndDay extends HeaderClosingEndDayKey{

	private String endDayWhCode;
	private Date endDayDate;
	private Date creationDate;
	private String creationUser;
	private Date confirmationDate;
	private String confirmationUser;
	private BigDecimal storeCount;
	private BigDecimal managerCount;
	private Integer statusId;

	public String getEndDayWhCode(){
		return endDayWhCode;
	}

	public void setEndDayWhCode(String endDayWhCode){
		this.endDayWhCode = endDayWhCode == null ? null : endDayWhCode.trim();
	}

	public Date getEndDayDate(){
		return endDayDate;
	}

	public void setEndDayDate(Date endDayDate){
		this.endDayDate = endDayDate;
	}

	public Date getCreationDate(){
		return creationDate;
	}

	public void setCreationDate(Date creationDate){
		this.creationDate = creationDate;
	}

	public String getCreationUser(){
		return creationUser;
	}

	public void setCreationUser(String creationUser){
		this.creationUser = creationUser == null ? null : creationUser.trim();
	}

	public Date getConfirmationDate(){
		return confirmationDate;
	}

	public void setConfirmationDate(Date confirmationDate){
		this.confirmationDate = confirmationDate;
	}

	public String getConfirmationUser(){
		return confirmationUser;
	}

	public void setConfirmationUser(String confirmationUser){
		this.confirmationUser = confirmationUser == null ? null : confirmationUser.trim();
	}

	public BigDecimal getStoreCount(){
		return storeCount;
	}

	public void setStoreCount(BigDecimal storeCount){
		this.storeCount = storeCount;
	}

	public BigDecimal getManagerCount(){
		return managerCount;
	}

	public void setManagerCount(BigDecimal managerCount){
		this.managerCount = managerCount;
	}

	public Integer getStatusId(){
		return statusId;
	}

	public void setStatusId(Integer statusId){
		this.statusId = statusId;
	}
}