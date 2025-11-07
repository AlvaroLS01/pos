package com.comerzzia.api.loyalty.service.coupons.links;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.comerzzia.api.core.service.exception.ApiException;
import com.comerzzia.api.loyalty.persistence.coupons.links.CouponLinkExample;
import com.comerzzia.api.loyalty.persistence.coupons.links.CouponLinkKey;
import com.comerzzia.api.loyalty.persistence.coupons.links.CouponLinkMapper;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

@Service
public class CouponsLinksServiceImpl implements CouponsLinksService {
   @Autowired
   CouponLinkMapper mapper;

   @Override
   @Transactional(rollbackFor=Exception.class)
   public void insert(IDatosSesion datosSesion, Long couponId, List<CouponLinkKey> links) throws ApiException {
      for (CouponLinkKey record : links) {
         insert(datosSesion, couponId, record);
      }      
   }
   

   @Override
   @Transactional(rollbackFor=Exception.class)
   public void insert(IDatosSesion datosSesion, Long couponId, CouponLinkKey record) throws ApiException {
      record.setUidActividad(datosSesion.getUidActividad());
      record.setCouponId(couponId);
      mapper.insert(record);
   }   
   
   @Override
   public List<CouponLinkKey> selectByCoupon(IDatosSesion datosSesion, Long couponId) {      
      CouponLinkExample example = new CouponLinkExample(datosSesion);
      example.or().andCouponIdEqualTo(couponId);
      
      return mapper.selectByExample(example); 
   }
}
