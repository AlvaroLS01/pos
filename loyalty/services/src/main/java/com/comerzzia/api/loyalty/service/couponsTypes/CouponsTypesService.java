package com.comerzzia.api.loyalty.service.couponsTypes;

import java.util.List;

import com.comerzzia.api.core.service.exception.ApiException;
import com.comerzzia.api.core.service.exception.NotFoundException;
import com.comerzzia.api.loyalty.persistence.couponsTypes.CouponType;
import com.comerzzia.api.loyalty.persistence.couponsTypes.CouponTypeExample;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

public interface CouponsTypesService {
   public static final Short GENERATION_MODE_ALEATORY = 0;   
   public static final Short GENERATION_MODE_SEQUENTIALLY = 1;
   public static final Short GENERATION_MODE_MANUAL = 2;
   
   CouponType insert(IDatosSesion datosSesion, CouponType newRecord) throws ApiException;

   CouponType selectByPrimaryKey(IDatosSesion datosSesion, String couponTypeCode);

   List<CouponType> selectByExample(CouponTypeExample example);

   int delete(IDatosSesion datosSesion, String couponTypeCode);

   CouponType selectByPrimaryKeyFromCache(IDatosSesion datosSesion, String couponTypeCode) throws NotFoundException;

}