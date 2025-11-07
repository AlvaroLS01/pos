package com.comerzzia.api.loyalty.service.operations.dto;

import java.util.ArrayList;
import java.util.List;

import com.comerzzia.api.loyalty.service.coupons.dto.CouponRedeemResult;

public class LoyaltySaleOperationResult {
   List<OperationResult> newCouponsResults;
   List<CouponRedeemResult> redeemResults;
   List<OperationResult> accountActivitiesResults;
   
   public LoyaltySaleOperationResult() {
      newCouponsResults = new ArrayList<OperationResult>();
      redeemResults = new ArrayList<CouponRedeemResult>();
      accountActivitiesResults = new ArrayList<OperationResult>(); 
   }
   
   public List<OperationResult> getNewCouponsResults() {
      return newCouponsResults;
   }
   public void setNewCouponsResults(List<OperationResult> newCouponsResults) {
      this.newCouponsResults = newCouponsResults;
   }
   public List<CouponRedeemResult> getRedeemResults() {
      return redeemResults;
   }
   public void setRedeemResults(List<CouponRedeemResult> redeemResults) {
      this.redeemResults = redeemResults;
   }
   public List<OperationResult> getAccountActivitiesResults() {
      return accountActivitiesResults;
   }
   public void setAccountActivitiesResults(List<OperationResult> accountActivitiesResults) {
      this.accountActivitiesResults = accountActivitiesResults;
   }
   
}
