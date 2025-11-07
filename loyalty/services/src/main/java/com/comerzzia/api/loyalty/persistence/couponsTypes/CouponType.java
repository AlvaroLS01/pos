package com.comerzzia.api.loyalty.persistence.couponsTypes;

import javax.ws.rs.QueryParam;

public class CouponType extends CouponTypeKey {
    @QueryParam("couponTypeName")
    private String couponTypeName;

    private Boolean defManualSelect;

    @QueryParam("prefix")
    private String prefix;

    private Short maxLength;

    @QueryParam("generationMode")
    private Short generationMode;

    public String getCouponTypeName() {
        return couponTypeName;
    }

    public void setCouponTypeName(String couponTypeName) {
        this.couponTypeName = couponTypeName == null ? null : couponTypeName.trim();
    }

    public Boolean getDefManualSelect() {
        return defManualSelect;
    }

    public void setDefManualSelect(Boolean defManualSelect) {
        this.defManualSelect = defManualSelect;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix == null ? null : prefix.trim();
    }

    public Short getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Short maxLength) {
        this.maxLength = maxLength;
    }

    public Short getGenerationMode() {
        return generationMode;
    }

    public void setGenerationMode(Short generationMode) {
        this.generationMode = generationMode;
    }
}