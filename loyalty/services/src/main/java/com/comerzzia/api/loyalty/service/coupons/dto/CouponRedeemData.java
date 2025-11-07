package com.comerzzia.api.loyalty.service.coupons.dto;

import java.math.BigDecimal;

public class CouponRedeemData {
   protected String couponCode;
   protected BigDecimal discount;
   protected BigDecimal saleAmount;
   
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
