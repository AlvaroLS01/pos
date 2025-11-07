package com.comerzzia.api.loyalty.persistence.coupons.dto;

import com.comerzzia.api.loyalty.persistence.coupons.Coupon;
import com.comerzzia.api.loyalty.persistence.coupons.uses.CouponUse;

public class CouponDTO extends Coupon {
   protected String loyalCustomerId;
   protected CouponUse uses;
   protected PromotionDTO promotion;

   public String getLoyalCustomerId() {
      return loyalCustomerId;
   }

   public void setLoyalCustomerId(String loyalCustomerId) {
      this.loyalCustomerId = loyalCustomerId;
   }

   public CouponUse getUses() {
      return uses;
   }

   public void setUses(CouponUse uses) {
      this.uses = uses;
   }

   public PromotionDTO getPromotion() {
      return promotion;
   }

   public void setPromotion(PromotionDTO promotion) {
      this.promotion = promotion;
   }

}
