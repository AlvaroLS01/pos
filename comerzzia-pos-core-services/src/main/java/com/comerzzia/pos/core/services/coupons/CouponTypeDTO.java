package com.comerzzia.pos.core.services.coupons;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponTypeDTO {
	private String couponTypeCode;
	private String couponTypeName;
	private Boolean defManualSelect;
	private String prefix;
	private Integer maxLength;
	private Integer generationMode;
}
