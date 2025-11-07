package com.comerzzia.api.loyalty.service.coupons.uses;

import java.math.BigDecimal;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.comerzzia.api.core.service.exception.ApiException;
import com.comerzzia.api.core.service.exception.GeneralErrorKeysConstants;
import com.comerzzia.api.loyalty.persistence.coupons.links.CouponLinkKey;
import com.comerzzia.api.loyalty.persistence.coupons.uses.CouponUse;
import com.comerzzia.api.loyalty.persistence.coupons.uses.CouponUseKey;
import com.comerzzia.api.loyalty.persistence.coupons.uses.CouponUseMapper;
import com.comerzzia.api.loyalty.service.coupons.links.CouponsLinksService;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

@Service
public class CouponsUsesServiceImpl implements CouponsUsesService {
   @Autowired
   CouponUseMapper mapper;
   
   @Autowired
   CouponsLinksService couponsLinksService;
   
   @Override
   @Transactional(rollbackFor=Exception.class)
   public void insert(IDatosSesion datosSesion, Long couponId, List<CouponUse> uses) throws ApiException {
      for (CouponUse record : uses) {
         insert(datosSesion, couponId, record);
      }      
   }
   
   @Override
   public void insert(IDatosSesion datosSesion, Long couponId, CouponUse record) throws ApiException {
      record.setUidActividad(datosSesion.getUidActividad());
      record.setCouponId(couponId);
      mapper.insert(record);
   }   
   @Override
   public int updateUses(IDatosSesion datosSesion, CouponUse currentRecord, CouponUse newRecord) {      
      return mapper.updateUsesSelective(currentRecord, newRecord);
   }
   
   @Override
   public void addUse(IDatosSesion datosSesion, CouponUseKey key, BigDecimal discount, BigDecimal sale, Date lastUse, String lastTerminalId) throws ApiException {
      key.setUidActividad(datosSesion.getUidActividad());
      
      int records = mapper.addUse(key, discount, sale, lastUse, lastTerminalId);
      
      // insert new record
      if (records == 0) {
         CouponUse record = new CouponUse();
         record.setClassId(key.getClassId());
         record.setObjectId(key.getObjectId());
         record.setUses(1);
         record.setTotalDiscount(discount);
         record.setTotalSale(sale);
         record.setLastTerminalId(lastTerminalId);
         record.setFirstUse(lastUse);
         record.setLastUse(lastUse);
         
         insert(datosSesion, key.getCouponId(), record);         
         
         // insert link
         try {
            couponsLinksService.insert(datosSesion, key.getCouponId(), new CouponLinkKey(key.getClassId(), key.getObjectId()));
         } catch (Exception e) {
            if(e.getCause() instanceof SQLIntegrityConstraintViolationException) { 
               // ignore duplicate key error
            }
            else{
               throw new ApiException(ApiException.STATUS_RESPONSE_ERROR_INTERNAL, GeneralErrorKeysConstants.ERROR_RECORD_INSERT, e.getMessage());
            }  
         }
      }
   }
}
