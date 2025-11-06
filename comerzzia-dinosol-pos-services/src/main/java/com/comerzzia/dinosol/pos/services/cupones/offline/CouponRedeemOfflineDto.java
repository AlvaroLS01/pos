package com.comerzzia.dinosol.pos.services.cupones.offline;

import java.math.BigDecimal;

public class CouponRedeemOfflineDto {

	private String couponCode;

	private BigDecimal discount;

	private BigDecimal saleAmount;

	public String getCouponCode() {
		return couponCode;
	}

	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}

	public BigDecimal getDiscount() {
		return discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	public BigDecimal getSaleAmount() {
		return saleAmount;
	}

	public void setSaleAmount(BigDecimal saleAmount) {
		this.saleAmount = saleAmount;
	}

}
