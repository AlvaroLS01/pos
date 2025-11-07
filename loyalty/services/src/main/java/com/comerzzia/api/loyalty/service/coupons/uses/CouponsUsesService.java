package com.comerzzia.api.loyalty.service.coupons.uses;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.comerzzia.api.core.service.exception.ApiException;
import com.comerzzia.api.loyalty.persistence.coupons.uses.CouponUse;
import com.comerzzia.api.loyalty.persistence.coupons.uses.CouponUseKey;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

public interface CouponsUsesService {

   void insert(IDatosSesion datosSesion, Long couponId, List<CouponUse> uses) throws ApiException;

   int updateUses(IDatosSesion datosSesion, CouponUse currentRecord, CouponUse newRecord);

   void insert(IDatosSesion datosSesion, Long couponId, CouponUse record) throws ApiException;

   void addUse(IDatosSesion datosSesion, CouponUseKey key, BigDecimal discount, BigDecimal sale, Date lastUse, String lastTerminalId) throws ApiException;

}