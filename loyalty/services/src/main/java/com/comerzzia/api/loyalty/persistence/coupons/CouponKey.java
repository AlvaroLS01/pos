package com.comerzzia.api.loyalty.persistence.coupons;

import javax.xml.bind.annotation.XmlTransient;

public class CouponKey {
    @XmlTransient
    private String uidActividad;

    @XmlTransient
    private Long couponId;

    public String getUidActividad() {
        return uidActividad;
    }

    public void setUidActividad(String uidActividad) {
        this.uidActividad = uidActividad == null ? null : uidActividad.trim();
    }

    public Long getCouponId() {
        return couponId;
    }

    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }
}