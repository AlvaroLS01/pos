package com.comerzzia.api.loyalty.service.operations.dto;

import java.util.List;

import com.comerzzia.api.loyalty.persistence.accounts.activities.AccountActivity;
import com.comerzzia.api.loyalty.service.coupons.dto.CouponRedeemData;
import com.comerzzia.api.loyalty.service.coupons.dto.NewCoupon;

public class LoyaltySaleOperation extends SaleOperationData {
   List<NewCoupon> newCoupons;
   List<CouponRedeemData> reedemCoupons;
   List<AccountActivity> accountActivities;
   
   public List<NewCoupon> getNewCoupons() {
      return newCoupons;
   }
   public void setNewCoupons(List<NewCoupon> newCoupons) {
      this.newCoupons = newCoupons;
   }
   public List<CouponRedeemData> getReedemCoupons() {
      return reedemCoupons;
   }
   public void setReedemCoupons(List<CouponRedeemData> reedemCoupons) {
      this.reedemCoupons = reedemCoupons;
   }
   public List<AccountActivity> getAccountActivities() {
      return accountActivities;
   }
   public void setAccountActivities(List<AccountActivity> accountActivities) {
      this.accountActivities = accountActivities;
   }
   
}
