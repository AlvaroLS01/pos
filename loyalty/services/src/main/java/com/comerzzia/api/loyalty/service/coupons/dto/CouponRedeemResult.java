package com.comerzzia.api.loyalty.service.coupons.dto;

public class CouponRedeemResult {
   protected String couponCode;
   protected String redeemError;
   
   public String getCouponCode() {
      return couponCode;
   }
   public void setCouponCode(String couponCode) {
      this.couponCode = couponCode;
   }
   public String getRedeemError() {
      return redeemError;
   }
   public void setRedeemError(String redeemError) {
      this.redeemError = redeemError;
   }
}
