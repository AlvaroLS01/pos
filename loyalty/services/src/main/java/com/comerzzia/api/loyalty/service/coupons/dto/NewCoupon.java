package com.comerzzia.api.loyalty.service.coupons.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;

import com.comerzzia.api.loyalty.persistence.coupons.links.CouponLinkKey;

public class NewCoupon {
    @Size(max=128)
    private String couponCode;

    @NotNull
    @Size(max=50)
    private String couponName;

    @Size(max=255)
    private String couponDescription;

    private Date startDate;

    private Date endDate;

    private Boolean manualSelection;

    private String couponTypeCode;

    @NotNull
    private Long promotionId;
    
    private Long priority=0L;

    private BigDecimal balance;

    private String imageUrl;
    
    private String loyalCustomerId;
    private Long customerMaxUses;
    
    @Size(max=60)
    private String createdByClassId;
    @Size(max=40)
    private String createdByObjectId;
    
    private List<CouponLinkKey> links;

    @XmlTransient
    private List<CouponRestriction> restrictions;

    private Map<String, String> couponDescriptionTranslations = new HashMap<String, String>();
    
    private Map<String, String> couponNameTranslations = new HashMap<String, String>();
    
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

   public String getLoyalCustomerId() {
      return loyalCustomerId;
   }

   public void setLoyalCustomerId(String loyalCustomerId) {
      this.loyalCustomerId = loyalCustomerId;
   }

   public Long getCustomerMaxUses() {
      return customerMaxUses;
   }

   public void setCustomerMaxUses(Long customerMaxUses) {
      this.customerMaxUses = customerMaxUses;
   }

   public String getCreatedByClassId() {
      return createdByClassId;
   }

   public void setCreatedByClassId(String createdByClassId) {
      this.createdByClassId = createdByClassId;
   }

   public String getCreatedByObjectId() {
      return createdByObjectId;
   }

   public void setCreatedByObjectId(String createdByObjectId) {
      this.createdByObjectId = createdByObjectId;
   }

   public List<CouponLinkKey> getLinks() {
      return links;
   }

   public void setLinks(List<CouponLinkKey> links) {
      this.links = links;
   }

   public List<CouponRestriction> getRestrictions() {
      return restrictions;
   }

   public void setRestrictions(List<CouponRestriction> restrictions) {
      this.restrictions = restrictions;
   }

	public Map<String, String> getCouponNameTranslations() {
		return couponNameTranslations;
	}
	
	public void setCouponNameTranslations(Map<String, String> couponNameTranslations) {
		this.couponNameTranslations = couponNameTranslations;
	}
	
	public Map<String, String> getCouponDescriptionTranslations() {
		return couponDescriptionTranslations;
	}
	
	public void setCouponDescriptionTranslations(Map<String, String> couponDescriptionTranslations) {
		this.couponDescriptionTranslations = couponDescriptionTranslations;
	}
}