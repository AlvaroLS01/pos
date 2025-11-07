package com.comerzzia.api.loyalty.service.couponsKeys;

import java.util.List;

import com.comerzzia.api.core.service.exception.ApiException;
import com.comerzzia.api.loyalty.persistence.coupons.dto.CouponDTO;
import com.comerzzia.api.loyalty.persistence.couponsKeys.CouponIssuanceKey;
import com.comerzzia.api.loyalty.persistence.couponsKeys.CouponIssuanceKeyExample;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

public interface CouponsKeysService {
   CouponIssuanceKey insert(IDatosSesion datosSesion, CouponIssuanceKey newRecord) throws ApiException;

   CouponIssuanceKey selectByPrimaryKey(IDatosSesion datosSesion, String key);

   List<CouponIssuanceKey> selectByExample(CouponIssuanceKeyExample example);

   int delete(IDatosSesion datosSesion, String key);

   CouponDTO createCoupon(IDatosSesion datosSesion, String key, String customerId) throws ApiException;

   CouponIssuanceKey update(IDatosSesion datosSesion, CouponIssuanceKey record) throws ApiException;
}