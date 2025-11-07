package com.comerzzia.api.loyalty.persistence.couponsTypes;

import javax.xml.bind.annotation.XmlTransient;

public class CouponTypeKey {
    @XmlTransient
    private String uidActividad;

    private String couponTypeCode;
    
    public CouponTypeKey() {      
    }
    
    public CouponTypeKey(String uidActividad, String couponTypeCode) {
       this.uidActividad = uidActividad;
       this.couponTypeCode = couponTypeCode;
    }
    
    public String getUidActividad() {
        return uidActividad;
    }


   public void setUidActividad(String uidActividad) {
        this.uidActividad = uidActividad == null ? null : uidActividad.trim();
    }

    public String getCouponTypeCode() {
        return couponTypeCode;
    }

    public void setCouponTypeCode(String couponTypeCode) {
        this.couponTypeCode = couponTypeCode == null ? null : couponTypeCode.trim();
    }

    @Override
    public String toString() {
       return "CouponTypeKey [couponTypeCode=" + couponTypeCode + "]";
    }
}