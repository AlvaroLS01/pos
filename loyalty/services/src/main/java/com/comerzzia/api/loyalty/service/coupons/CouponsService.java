package com.comerzzia.api.loyalty.service.coupons;

import java.util.Date;
import java.util.List;

import com.comerzzia.api.core.service.exception.ApiException;
import com.comerzzia.api.loyalty.persistence.coupons.Coupon;
import com.comerzzia.api.loyalty.persistence.coupons.dto.CouponCustomerUseDTO;
import com.comerzzia.api.loyalty.persistence.coupons.dto.CouponDTO;
import com.comerzzia.api.loyalty.persistence.coupons.dto.CouponsKpiDTO;
import com.comerzzia.api.loyalty.service.coupons.dto.CouponRedeemResult;
import com.comerzzia.api.loyalty.service.coupons.dto.CouponsFilter;
import com.comerzzia.api.loyalty.service.coupons.dto.NewCoupon;
import com.comerzzia.api.loyalty.service.coupons.dto.NewCouponForCustomers;
import com.comerzzia.api.loyalty.service.coupons.dto.RedeemCoupon;
import com.comerzzia.api.loyalty.service.coupons.dto.RedeemCoupons;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

public interface CouponsService {
   public static final String LY_COUPON_NOT_ACTIVE_UNTIL = "LY_COUPON_NOT_ACTIVE_UNTIL";
   public static final String LY_COUPON_EXPIRED_AT = "LY_COUPON_EXPIRED_AT";
   public static final String LY_COUPON_USED = "LY_COUPON_USED";
   public static final String LY_COUPON_NOT_EXISTS = "LY_COUPON_NOT_EXISTS";
   public static final String LY_COUPON_BALANCE_DISCOUNT_ERROR = "LY_COUPON_BALANCE_DISCOUNT_ERROR";
   public static final String LY_COUPON_LOCKED_BY = "LY_COUPON_LOCKED_BY";
   public static final String LY_COUPON_CONCURRENCE_ERROR = "LY_COUPON_CONCURRENCE_ERROR";
   public static final String LY_COUPON_CONCURRENCE_CUSTOMER_ERROR = "LY_COUPON_CONCURRENCE_CUSTOMER_ERROR";
   public static final String LY_COUPON_CUSTOMER_ERROR = "LY_COUPON_CUSTOMER_ERROR";
   
   public static final String LY_COUPON_NOT_ACTIVE = "LY_COUPON_NOT_ACTIVE";
   public static final String LY_COUPON_ACTIVE = "LY_COUPON_ACTIVE";
   public static final String LY_COUPON_IS_USED = "LY_COUPON_IS_USED";
   
   public static final String ID_CLASS_LOYAL_CUSTOMER_ID = "F_FIDELIZADOS_TBL.ID_FIDELIZADO";
   public static final String ID_CLASS_COUPON_ID = "LY_COUPONS_TBL.COUPON_ID";
   
   public static final String ID_CLASS_STORE_ID = "D_ALMACENES_TBL.CODALM";  
   public static final String ID_CLASS_SALES_DOCUMENT_UID = "D_TICKETS_TBL.UID_TICKET";
   
   public static final String ID_CLASS_CREATED_CLASS_ID = "LY_COUPONS_TBL.CREATED_BY_CLASS_ID";
   public static final String ID_CLASS_CREATED_OBJECT_ID = "LY_COUPONS_TBL.CREATED_BY_OBJECT_ID";
   
   public static final String ID_CLASS_COUPON_NAME = "LY_COUPONS_TBL.COUPON_NAME";
   public static final String ID_CLASS_COUPON_DESCRIPTION = "LY_COUPONS_TBL.COUPON_DESCRIPTION";

   List<CouponDTO> selectByCustomer(IDatosSesion datosSesion, CouponsFilter filter);

   CouponDTO selectByUk(IDatosSesion datosSesion, String couponCode);

   void validate(IDatosSesion datosSesion, CouponCustomerUseDTO couponCustomerUseDTO, Date validationDate, String loyalCustomerId) throws ApiException;

   Coupon insert(IDatosSesion datosSesion, NewCoupon newRecord) throws ApiException;
   
   List<String> insertMultiple(IDatosSesion datosSesion, List<NewCoupon> newRecords) throws ApiException;

   List<String> insertMultiple(IDatosSesion datosSesion, NewCouponForCustomers newCouponForCustomers)
         throws ApiException;

   List<CouponRedeemResult> redeem(IDatosSesion datosSesion, RedeemCoupons redeem);

   void redeem(IDatosSesion datosSesion, RedeemCoupon redeemCoupon) throws ApiException;

   CouponCustomerUseDTO selectByUkAndCustomer(IDatosSesion datosSesion, String couponCode, String loyalCustomerId);

   List<CouponDTO> selectByFilter(IDatosSesion datosSesion, CouponsFilter filter);

   CouponsKpiDTO selectKPIS(IDatosSesion datosSesion, String loyalCustomerId, Date startDate, Date endDate);

   CouponsKpiDTO selectKPIS(IDatosSesion datosSesion, Date startDate, Date endDate);

   CouponDTO deactivate(IDatosSesion datosSesion, String couponCode) throws ApiException;

   boolean exitsCouponCode(IDatosSesion datosSesion, String couponCode);

   CouponDTO activate(IDatosSesion datosSesion, String couponCode) throws ApiException;
}