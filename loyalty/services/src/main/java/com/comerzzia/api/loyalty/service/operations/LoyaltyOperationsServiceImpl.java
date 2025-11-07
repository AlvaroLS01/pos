package com.comerzzia.api.loyalty.service.operations;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.api.core.service.exception.ApiException;
import com.comerzzia.api.loyalty.persistence.accounts.activities.AccountActivity;
import com.comerzzia.api.loyalty.service.accounts.activities.AccountsActivitiesService;
import com.comerzzia.api.loyalty.service.coupons.CouponsService;
import com.comerzzia.api.loyalty.service.coupons.dto.CouponRedeemData;
import com.comerzzia.api.loyalty.service.coupons.dto.CouponRedeemResult;
import com.comerzzia.api.loyalty.service.coupons.dto.NewCoupon;
import com.comerzzia.api.loyalty.service.coupons.dto.RedeemCoupon;
import com.comerzzia.api.loyalty.service.operations.dto.LoyaltySaleOperation;
import com.comerzzia.api.loyalty.service.operations.dto.LoyaltySaleOperationResult;
import com.comerzzia.api.loyalty.service.operations.dto.OperationResult;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

@Service
public class LoyaltyOperationsServiceImpl implements LoyaltyOperationsService {
   private static final Logger log = LoggerFactory.getLogger(LoyaltyOperationsServiceImpl.class);
   
   @Autowired
   CouponsService couponsService;
   
   @Autowired
   AccountsActivitiesService accountsActivitiesService;
   
   @Override
   public LoyaltySaleOperationResult registerLoyaltyOperation(IDatosSesion datosSesion, LoyaltySaleOperation loyaltySaleOperation) throws ApiException {
      LoyaltySaleOperationResult loyaltySaleOperationResult = new LoyaltySaleOperationResult();
      
      // insert new coupons
      if (loyaltySaleOperation.getNewCoupons() != null) {
	      for (NewCoupon newCoupon : loyaltySaleOperation.getNewCoupons()) {
	         OperationResult operationResult = new OperationResult();
	         operationResult.setOperationData(newCoupon);
	         try {
	            couponsService.insert(datosSesion, newCoupon);
	         } catch (Exception e) {
	            operationResult.setError(e.getMessage());
	            log.warn("Error creating new coupon for Loyalty Operation. Coupon " + newCoupon.getCouponCode() + " message: " + e.getMessage() );
	         }
	         
	         loyaltySaleOperationResult.getNewCouponsResults().add(operationResult);
	      }
      }
      
      // redeem coupons
      if (loyaltySaleOperation.getReedemCoupons() != null) {
	      for (CouponRedeemData couponRedeemData : loyaltySaleOperation.getReedemCoupons()) {
	         CouponRedeemResult couponRedeemResult = new CouponRedeemResult();
	         couponRedeemResult.setCouponCode(couponRedeemData.getCouponCode());
	         
	         RedeemCoupon redeemCoupon = new RedeemCoupon();
	
	         // copy sale properties
	         try {
	            BeanUtils.copyProperties(redeemCoupon, loyaltySaleOperation);
	         } catch (IllegalAccessException | InvocationTargetException e) {
	            throw new ApiException("Internal error in copyProperties",  e);
	         }
	         
	         // coupon to redeem
	         redeemCoupon.setCouponRedeemData(couponRedeemData);
	         
	         couponsService.redeem(datosSesion, redeemCoupon);
	         
	         loyaltySaleOperationResult.getRedeemResults().add(couponRedeemResult);
	      }
      }
      
      // creating cards accounts activities
      if (loyaltySaleOperation.getAccountActivities() != null) {
	      for (AccountActivity accountActivity : loyaltySaleOperation.getAccountActivities()) {
	         OperationResult operationResult = new OperationResult();
	         operationResult.setOperationData(accountActivity);
	         
	         try {
	            accountsActivitiesService.insert(accountActivity, datosSesion);
	         } catch (Exception e) {
	            operationResult.setError(e.getMessage());
	            log.warn("Error inserting accounts activities for Loyalty Operation. Cartd " + accountActivity.getIdTarjeta() + " message: " + e.getMessage() );
	         }
	         
	         loyaltySaleOperationResult.getAccountActivitiesResults().add(operationResult);
	      }
      }
      
      return loyaltySaleOperationResult;
   }
}
