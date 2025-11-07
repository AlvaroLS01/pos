package com.comerzzia.api.loyalty.service.coupons.dto;

import java.util.List;

import com.comerzzia.api.loyalty.service.operations.dto.SaleOperationData;

public class RedeemCoupons extends SaleOperationData {
   List<CouponRedeemData> couponsRedeemData;

   public List<CouponRedeemData> getCoupons() {
      return couponsRedeemData;
   }

   public void setCoupons(List<CouponRedeemData> coupons) {
      this.couponsRedeemData = coupons;
   }
}
