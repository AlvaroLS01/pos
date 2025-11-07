package com.comerzzia.api.loyalty.persistence.couponsKeys;

import java.math.BigDecimal;
import java.util.Date;

public class CouponIssuanceKey extends CouponIssuanceKeyKey {
    private Date keyStartDate;

    private Date keyEndDate;

    private Boolean active;

    private Long maxUses;

    private Short vigenceDays;

    private String couponName;

    private String couponDescription;

    private Boolean manualSelection;

    private String couponTypeCode;

    private Long promotionId;

    private Long priority;

    private BigDecimal balance;

    private String imageUrl;
        
    public Date getKeyStartDate() {
        return keyStartDate;
    }

    public void setKeyStartDate(Date keyStartDate) {
        this.keyStartDate = keyStartDate;
    }

    public Date getKeyEndDate() {
        return keyEndDate;
    }

    public void setKeyEndDate(Date keyEndDate) {
        this.keyEndDate = keyEndDate;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Long getMaxUses() {
        return maxUses;
    }

    public void setMaxUses(Long maxUses) {
        this.maxUses = maxUses;
    }

    public Short getVigenceDays() {
        return vigenceDays;
    }

    public void setVigenceDays(Short vigenceDays) {
        this.vigenceDays = vigenceDays;
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
}