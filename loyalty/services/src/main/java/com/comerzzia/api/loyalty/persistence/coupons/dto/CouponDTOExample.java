package com.comerzzia.api.loyalty.persistence.coupons.dto;

import com.comerzzia.api.loyalty.persistence.coupons.CouponExample;
import com.comerzzia.api.loyalty.service.coupons.dto.CouponsFilter;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

@SuppressWarnings("serial")
public class CouponDTOExample extends CouponExample {
   protected CouponsFilter filter;

   public CouponDTOExample(IDatosSesion datosSesion) {
      super(datosSesion);
    }
   
   public CouponsFilter getFilter() {
      return filter;
   }

   public void setFilter(CouponsFilter filter) {
      this.filter = filter;
   }


}
