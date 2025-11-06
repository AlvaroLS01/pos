package com.comerzzia.dinosol.pos.services.cupones;

import java.math.BigDecimal;
import java.util.Date;

import com.comerzzia.pos.services.cupones.CuponGeneradoDto;

public class GeneratedCouponDto extends CuponGeneradoDto {

	private String couponTypeCode;

	private Long promotionId;

	private Date startDate;

	private Date endDate;

	public GeneratedCouponDto(String codigoCupon, BigDecimal importeCupon) {
		super(codigoCupon, importeCupon);
	}

	public String getCouponTypeCode() {
		return couponTypeCode;
	}

	public void setCouponTypeCode(String couponTypeCode) {
		this.couponTypeCode = couponTypeCode;
	}

	public Long getPromotionId() {
		return promotionId;
	}

	public void setPromotionId(Long promotionId) {
		this.promotionId = promotionId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

}
