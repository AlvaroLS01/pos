package com.comerzzia.api.loyalty.service.coupons.dto;

import com.comerzzia.api.loyalty.service.operations.dto.SaleOperationData;

public class RedeemCoupon extends SaleOperationData {
   CouponRedeemData couponRedeemData;

   public CouponRedeemData getCouponRedeemData() {
      return couponRedeemData;
   }

   public void setCouponRedeemData(CouponRedeemData couponRedeemData) {
      this.couponRedeemData = couponRedeemData;
   }

   
}
