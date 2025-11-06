package com.comerzzia.dinosol.pos.services.cupones.offline;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class LoyaltySaleOperationOfflineDto {

	private Date date;

	private String storeId;

	private String terminalId;

	private String lockByTerminalId;

	private String loyalCustomerId;

	private String ticketUid;

	private String tillId;

	private List<CouponRedeemOfflineDto> reedemCoupons;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public String getTerminalId() {
		return terminalId;
	}

	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}

	public String getLoyalCustomerId() {
		return loyalCustomerId;
	}

	public void setLoyalCustomerId(String loyalCustomerId) {
		this.loyalCustomerId = loyalCustomerId;
	}

	public String getTicketUid() {
		return ticketUid;
	}

	public void setTicketUid(String ticketUid) {
		this.ticketUid = ticketUid;
	}

	public String getTillId() {
		return tillId;
	}

	public void setTillId(String tillId) {
		this.tillId = tillId;
	}

	public List<CouponRedeemOfflineDto> getReedemCoupons() {
		return reedemCoupons;
	}

	public void setReedemCoupons(List<CouponRedeemOfflineDto> reedemCoupons) {
		this.reedemCoupons = reedemCoupons;
	}

	public String getLockByTerminalId() {
		return lockByTerminalId;
	}

	public void setLockByTerminalId(String lockByTerminalId) {
		this.lockByTerminalId = lockByTerminalId;
	}

}
