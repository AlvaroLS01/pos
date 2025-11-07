package com.comerzzia.api.loyalty.service.coupons;

import com.comerzzia.api.core.service.exception.ApiException;
import com.comerzzia.api.loyalty.service.coupons.dto.NewCoupon;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

public interface CouponsCodeGeneratorService {
   public static final String LY_COUPON_CODE_NULL_IN_MANUAL_MODE = "LY_COUPON_CODE_NULL_IN_MANUAL_MODE";
   public static final String LY_COUPON_CODE_MAX_ATTEMPTS = "LY_COUPON_CODE_MAX_ATTEMPTS";
   
   public static final String COUNTER_NOT_FOUND = "counters.COUNTER_NOT_FOUND";

   String generateCouponCode(IDatosSesion datosSesion, NewCoupon newCoupon) throws ApiException;
}