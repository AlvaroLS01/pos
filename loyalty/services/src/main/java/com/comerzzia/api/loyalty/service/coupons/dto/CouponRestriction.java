package com.comerzzia.api.loyalty.service.coupons.dto;

public class CouponRestriction {
    private String classId;

    private String objectId;
    
    private Long maxUses;    

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

   public Long getMaxUses() {
      return maxUses;
   }

   public void setMaxUses(Long maxUses) {
      this.maxUses = maxUses;
   }

}