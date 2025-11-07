package com.comerzzia.api.loyalty.persistence.couponsKeys;

import javax.xml.bind.annotation.XmlTransient;

public class CouponIssuanceKeyKey {
    @XmlTransient
    private String uidActividad;

    private String couponKey;
    
    public CouponIssuanceKeyKey() {       
    }
    
    public CouponIssuanceKeyKey(String uidActividad, String couponKey) {
       this.uidActividad = uidActividad;
       this.couponKey = couponKey;
    }

    public String getUidActividad() {
        return uidActividad;
    }

    public void setUidActividad(String uidActividad) {
        this.uidActividad = uidActividad == null ? null : uidActividad.trim();
    }

    public String getCouponKey() {
        return couponKey;
    }

    public void setCouponKey(String couponKey) {
        this.couponKey = couponKey == null ? null : couponKey.trim();
    }
}