package com.comerzzia.api.loyalty.persistence.coupons.dto;

import com.comerzzia.api.loyalty.persistence.coupons.uses.CouponUse;

public class CouponCustomerUseDTO extends CouponDTO {
   protected CouponUse customerUses;

   public CouponUse getCustomerUses() {
      return customerUses;
   }

   public void setCustomerUses(CouponUse customerUses) {
      this.customerUses = customerUses;
   }

}
