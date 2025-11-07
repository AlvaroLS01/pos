package com.comerzzia.api.loyalty.service.couponsKeys;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.api.core.service.exception.ApiException;
import com.comerzzia.api.core.service.exception.BadRequestException;
import com.comerzzia.api.core.service.exception.GeneralErrorKeysConstants;
import com.comerzzia.api.loyalty.persistence.coupons.Coupon;
import com.comerzzia.api.loyalty.persistence.coupons.dto.CouponDTO;
import com.comerzzia.api.loyalty.persistence.couponsKeys.CouponIssuanceKey;
import com.comerzzia.api.loyalty.persistence.couponsKeys.CouponIssuanceKeyExample;
import com.comerzzia.api.loyalty.persistence.couponsKeys.CouponIssuanceKeyKey;
import com.comerzzia.api.loyalty.persistence.couponsKeys.CouponIssuanceKeyMapper;
import com.comerzzia.api.loyalty.service.coupons.CouponsService;
import com.comerzzia.api.loyalty.service.coupons.dto.NewCoupon;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

@Service
public class CouponsKeysServiceImpl implements CouponsKeysService {
   @Autowired
   CouponIssuanceKeyMapper mapper;
   
   @Autowired
   CouponsService couponsService;
   
   @Override
   public CouponIssuanceKey insert(IDatosSesion datosSesion, CouponIssuanceKey newRecord) throws ApiException {
      newRecord.setUidActividad(datosSesion.getUidActividad());
      
      try {
         mapper.insert(newRecord);
      } catch (Exception e) {
         if(e.getCause() instanceof SQLIntegrityConstraintViolationException) { 
            throw new BadRequestException(ApiException.STATUS_RESPONSE_ERROR_CONFLICT_STATE, GeneralErrorKeysConstants.ERROR_RECORD_DUPLICATE_KEY, e.getMessage());
         }
         else{
            throw new ApiException(ApiException.STATUS_RESPONSE_ERROR_INTERNAL, GeneralErrorKeysConstants.ERROR_RECORD_INSERT, e.getMessage());
         }        
      }
      
      return newRecord;
   }
   
   @Override
   public CouponIssuanceKey update(IDatosSesion datosSesion, CouponIssuanceKey record) throws ApiException {
      record.setUidActividad(datosSesion.getUidActividad());
      
      try {
         mapper.updateByPrimaryKey(record);
      } catch (Exception e) {
         if(e.getCause() instanceof SQLIntegrityConstraintViolationException) { 
            throw new BadRequestException(ApiException.STATUS_RESPONSE_ERROR_CONFLICT_STATE, GeneralErrorKeysConstants.ERROR_RECORD_DUPLICATE_KEY, e.getMessage());
         }
         else{
            throw new ApiException(ApiException.STATUS_RESPONSE_ERROR_INTERNAL, GeneralErrorKeysConstants.ERROR_RECORD_INSERT, e.getMessage());
         }        
      }
      
      return record;
   }
   
   @Override
   public CouponIssuanceKey selectByPrimaryKey(IDatosSesion datosSesion, String key) {
      return mapper.selectByPrimaryKey(new CouponIssuanceKeyKey(datosSesion.getUidActividad(), key));
   }
      
   @Override
   public List<CouponIssuanceKey> selectByExample(CouponIssuanceKeyExample example) {
      return mapper.selectByExample(example);
   }  

   @Override
   public int delete(IDatosSesion datosSesion, String key) {
      return mapper.deleteByPrimaryKey(new CouponIssuanceKeyKey(datosSesion.getUidActividad(), key));
   }
   
   @Override
   public CouponDTO createCoupon(IDatosSesion datosSesion, String key, String customerId) throws ApiException {
      CouponIssuanceKey issuanceKey = selectByPrimaryKey(datosSesion, key);
      
      NewCoupon newRecord = new NewCoupon();
      newRecord.setCouponName(issuanceKey.getCouponName());
      newRecord.setCouponDescription(issuanceKey.getCouponDescription());
      
      if (StringUtils.isEmpty(issuanceKey.getCouponTypeCode())) {
        newRecord.setCouponTypeCode("MANUAL");
        newRecord.setCouponCode(key + "-" + customerId.toString());         
      } else {         
        newRecord.setCouponTypeCode(issuanceKey.getCouponTypeCode());
      }
      
      newRecord.setPromotionId(issuanceKey.getPromotionId());
      newRecord.setLoyalCustomerId(customerId);
      newRecord.setCustomerMaxUses(issuanceKey.getMaxUses());

      newRecord.setBalance(issuanceKey.getBalance());
      newRecord.setManualSelection(issuanceKey.getManualSelection());
      newRecord.setPriority(issuanceKey.getPriority());
      
      if (issuanceKey.getKeyStartDate() != null) {
         newRecord.setStartDate(issuanceKey.getKeyStartDate());
      }
      
      if (issuanceKey.getKeyEndDate() != null) {
         newRecord.setEndDate(issuanceKey.getKeyEndDate());
      } else {      
         if (issuanceKey.getVigenceDays() != null && issuanceKey.getVigenceDays() > 0) {
            newRecord.setEndDate(DateUtils.truncate(DateUtils.addDays(newRecord.getStartDate() == null ? new Date() : newRecord.getStartDate(), issuanceKey.getVigenceDays()), Calendar.DAY_OF_MONTH));
         }
      }
      
      newRecord.setCreatedByClassId("LY_COUPONS_KEYS_TBL.COUPON_KEY");
      newRecord.setCreatedByObjectId(key);
      
      
      Coupon coupon = couponsService.insert(datosSesion, newRecord);
      
      return couponsService.selectByUkAndCustomer(datosSesion, coupon.getCouponCode(), customerId);      
   }
}
