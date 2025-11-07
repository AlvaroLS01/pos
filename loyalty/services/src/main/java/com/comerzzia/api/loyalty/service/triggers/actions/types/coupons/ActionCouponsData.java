package com.comerzzia.api.loyalty.service.triggers.actions.types.coupons;

import com.comerzzia.api.loyalty.service.coupons.dto.NewCoupon;

public class ActionCouponsData extends NewCoupon {
   protected Integer vigenceStartDays;
   protected Integer vigenceEndDays;
   
   public Integer getVigenceStartDays() {
      return vigenceStartDays;
   }
   public void setVigenceStartDays(Integer vigenceStartDays) {
      this.vigenceStartDays = vigenceStartDays;
   }
   public Integer getVigenceEndDays() {
      return vigenceEndDays;
   }
   public void setVigenceEndDays(Integer vigenceEndDays) {
      this.vigenceEndDays = vigenceEndDays;
   }
}
