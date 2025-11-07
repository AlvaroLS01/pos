package com.comerzzia.api.loyalty.service.coupons.dto;

import java.util.List;

public class NewCouponForCustomers {
   protected NewCoupon couponTemplate;
   protected List<String> loyalCustomerList;
   
   public NewCoupon getCouponTemplate() {
      return couponTemplate;
   }
   public void setCouponTemplate(NewCoupon couponTemplate) {
      this.couponTemplate = couponTemplate;
   }
   public List<String> getLoyalCustomerList() {
      return loyalCustomerList;
   }
   public void setLoyalCustomerList(List<String> loyalCustomerList) {
      this.loyalCustomerList = loyalCustomerList;
   }
}
