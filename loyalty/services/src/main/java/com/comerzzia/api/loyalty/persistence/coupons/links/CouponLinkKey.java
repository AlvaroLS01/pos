package com.comerzzia.api.loyalty.persistence.coupons.links;

import javax.xml.bind.annotation.XmlTransient;

public class CouponLinkKey {
    @XmlTransient
    private String uidActividad;

    private String classId;

    private String objectId;

    @XmlTransient
    private Long couponId;

    public CouponLinkKey() {
       
    }
    
    public CouponLinkKey(String classId, String objectId) {
      this.classId = classId;
      this.objectId = objectId;
   }

   public String getUidActividad() {
        return uidActividad;
    }

    public void setUidActividad(String uidActividad) {
        this.uidActividad = uidActividad == null ? null : uidActividad.trim();
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId == null ? null : classId.trim();
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId == null ? null : objectId.trim();
    }

    public Long getCouponId() {
        return couponId;
    }

    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }
}