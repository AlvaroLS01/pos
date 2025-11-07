package com.comerzzia.api.loyalty.service.coupons.links;

import java.util.List;

import com.comerzzia.api.core.service.exception.ApiException;
import com.comerzzia.api.loyalty.persistence.coupons.links.CouponLinkKey;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

public interface CouponsLinksService {

   void insert(IDatosSesion datosSesion, Long couponId, List<CouponLinkKey> links) throws ApiException;

   List<CouponLinkKey> selectByCoupon(IDatosSesion datosSesion, Long couponId);

   void insert(IDatosSesion datosSesion, Long couponId, CouponLinkKey record) throws ApiException;

}