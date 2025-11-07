package com.comerzzia.api.loyalty.persistence.coupons;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlTransient;

public class Coupon extends CouponKey {
    private String couponCode;

    private String couponName;

    private String couponDescription;

    private Date startDate;

    private Date endDate;

    private Boolean manualSelection;

    @XmlTransient
    private String couponTypeCode;

    private Long promotionId;

    private Long priority;

    private BigDecimal balance;

    private String imageUrl;

    private Boolean active;
    
    private Date creationDate;

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode == null ? null : couponCode.trim();
    }

    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName == null ? null : couponName.trim();
    }

    public String getCouponDescription() {
        return couponDescription;
    }

    public void setCouponDescription(String couponDescription) {
        this.couponDescription = couponDescription == null ? null : couponDescription.trim();
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

    public Boolean getManualSelection() {
        return manualSelection;
    }

    public void setManualSelection(Boolean manualSelection) {
        this.manualSelection = manualSelection;
    }

    public String getCouponTypeCode() {
        return couponTypeCode;
    }

    public void setCouponTypeCode(String couponTypeCode) {
        this.couponTypeCode = couponTypeCode == null ? null : couponTypeCode.trim();
    }

    public Long getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Long promotionId) {
        this.promotionId = promotionId;
    }

    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl == null ? null : imageUrl.trim();
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

   public Date getCreationDate() {
      return creationDate;
   }

   public void setCreationtDate(Date creationDate) {
      this.creationDate = creationDate;
   }
}