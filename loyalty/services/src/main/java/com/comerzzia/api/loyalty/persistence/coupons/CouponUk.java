package com.comerzzia.api.loyalty.persistence.coupons;

public class CouponUk {
    private String uidActividad;

    private String couponCode;

    public String getUidActividad() {
        return uidActividad;
    }

    public void setUidActividad(String uidActividad) {
        this.uidActividad = uidActividad == null ? null : uidActividad.trim();
    }

   public String getCouponCode() {
      return couponCode;
   }

   public void setCouponCode(String couponCode) {
      this.couponCode = couponCode;
   }

    
}