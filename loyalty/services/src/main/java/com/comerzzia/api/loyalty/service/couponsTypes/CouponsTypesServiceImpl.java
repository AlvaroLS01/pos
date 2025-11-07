package com.comerzzia.api.loyalty.service.couponsTypes;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.api.core.service.exception.ApiException;
import com.comerzzia.api.core.service.exception.BadRequestException;
import com.comerzzia.api.core.service.exception.GeneralErrorKeysConstants;
import com.comerzzia.api.core.service.exception.NotFoundException;
import com.comerzzia.api.loyalty.persistence.couponsTypes.CouponType;
import com.comerzzia.api.loyalty.persistence.couponsTypes.CouponTypeExample;
import com.comerzzia.api.loyalty.persistence.couponsTypes.CouponTypeKey;
import com.comerzzia.api.loyalty.persistence.couponsTypes.CouponTypeMapper;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

@Service
public class CouponsTypesServiceImpl implements CouponsTypesService {
   protected Map<String, CouponType> couponTypeCache = new HashMap<String, CouponType>();
   
   @Autowired
   CouponTypeMapper mapper;
   
   @Override
   public CouponType insert(IDatosSesion datosSesion, CouponType newRecord) throws ApiException {
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
   public CouponType selectByPrimaryKey(IDatosSesion datosSesion, String couponTypeCode) {
      return mapper.selectByPrimaryKey(new CouponTypeKey(datosSesion.getUidActividad(), couponTypeCode));
   }
   
   @Override
   public CouponType selectByPrimaryKeyFromCache(IDatosSesion datosSesion, String couponTypeCode) throws NotFoundException {
      if (StringUtils.isEmpty(couponTypeCode)) {
         couponTypeCode = "DEFAULT";         
      }
      
      CouponType result = couponTypeCache.get(couponTypeCode);
      
      if (result == null) {
         result = mapper.selectByPrimaryKey(new CouponTypeKey(datosSesion.getUidActividad(), couponTypeCode));
         
         if (result == null) {
            if ("DEFAULT".equals(couponTypeCode)) {
               // return default values for default coupon type
               result = new CouponType();
               result.setPrefix("9");
               result.setDefManualSelect(true);
               result.setMaxLength((short)8);
               result.setGenerationMode(CouponsTypesService.GENERATION_MODE_ALEATORY);
            } else {
               throw new NotFoundException();
            }
         }
                  
         couponTypeCache.put(couponTypeCode, result);
      }
      
      return result;
   }
   
   @Override
   public List<CouponType> selectByExample(CouponTypeExample example) {
      return mapper.selectByExample(example);
   }  

   @Override
   public int delete(IDatosSesion datosSesion, String couponTypeCode) {
      // TODO: Buscar cupones emitidos y si hay alguno, abortar
      
      return mapper.deleteByPrimaryKey(new CouponTypeKey(datosSesion.getUidActividad(), couponTypeCode));
   }
}
