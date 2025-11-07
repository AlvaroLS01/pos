package com.comerzzia.api.loyalty.persistence.coupons.dto;

import java.util.Date;

public class PromotionDTO {
   private Long promotionId;
   private String description;
   private Date startDate;
   private Date endDate;
   
   public Long getPromotionId() {
      return promotionId;
   }
   public void setPromotionId(Long promotionId) {
      this.promotionId = promotionId;
   }
   public String getDescription() {
      return description;
   }
   public void setDescription(String description) {
      this.description = description;
   }
   public Date getStartDate() {
      return startDate;
   }
   public void setStartDate(Date startDate) {
      this.startDate = startDate;
   }
   public Date getEndDate() {
      return endDate;
   }
   public void setEndDate(Date endDate) {
      this.endDate = endDate;
   }

}
