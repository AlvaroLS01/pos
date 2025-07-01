package com.comerzzia.pos.core.services.coupons;

import com.comerzzia.omnichannel.facade.model.basket.header.BasketLoyalCustomerCoupon;

public interface CouponsService {
	boolean isCouponCode(String code);

	BasketLoyalCustomerCoupon validateCoupon(String couponCode, Long loyalCustomerId);

}