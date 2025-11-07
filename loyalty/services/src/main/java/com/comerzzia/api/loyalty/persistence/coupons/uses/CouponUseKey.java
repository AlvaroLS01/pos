package com.comerzzia.api.loyalty.persistence.coupons.uses;

import javax.xml.bind.annotation.XmlTransient;

@XmlTransient
public class CouponUseKey {
    private String uidActividad;
    
    private String classId;

    private String objectId;

    private Long couponId;
    
    public CouponUseKey() {
    }

    public CouponUseKey(String uidActividad, String classId, String objectId, Long couponId) {
      this.uidActividad = uidActividad;
      this.classId = classId;
      this.objectId = objectId;
      this.couponId = couponId;
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

   @Override
   public String toString() {
      return "[classId=" + classId + ", objectId=" + objectId + ", couponId=" + couponId + "]";
   }
}