package com.comerzzia.dinosol.pos.services.cupones;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

public class CustomerCouponDTO {

	public static final Integer CUPON_ANTIGUO = 1;
	public static final Integer CUPON_NUEVO = 2;

	private String couponCode;

	private String couponName;

	private String couponDescription;

	private Date startDate;

	private Date endDate;

	private Boolean manualSelection;

	private Long promotionId;

	private Long priority;

	private BigDecimal balance;

	private String imageUrl;

	private Boolean active;

	private Date creationDate;

	private Long loyalCustomerId;

	private Date creationtDate;

	private boolean validationRequired;
	
	private Integer version;
	
	private boolean fromLoyaltyRequest; 

	public CustomerCouponDTO() {
		super();
	}

	public CustomerCouponDTO(String couponCode, boolean validationRequired, Integer version) {
		super();
		this.couponCode = couponCode;
		this.validationRequired = validationRequired;
		this.version = version;
	}

	public String getCouponCode() {
		return couponCode;
	}

	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}

	public String getCouponName() {
		return couponName;
	}

	public void setCouponName(String couponName) {
		this.couponName = couponName;
	}

	public String getCouponDescription() {
		return couponDescription;
	}

	public void setCouponDescription(String couponDescription) {
		this.couponDescription = couponDescription;
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
		this.imageUrl = imageUrl;
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

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Long getLoyalCustomerId() {
		return loyalCustomerId;
	}

	public void setLoyalCustomerId(Long loyalCustomerId) {
		this.loyalCustomerId = loyalCustomerId;
	}

	public Date getCreationtDate() {
		return creationtDate;
	}

	public void setCreationtDate(Date creationtDate) {
		this.creationtDate = creationtDate;
	}

	public boolean isValidationRequired() {
		return validationRequired;
	}

	public void setValidationRequired(boolean validationRequired) {
		this.validationRequired = validationRequired;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	public boolean isCuponAntiguo() {
		return this.version.equals(CUPON_ANTIGUO);
	}
	
	public boolean isCuponNuevo() {
		return this.version.equals(CUPON_NUEVO);
	}

	public boolean isFromLoyaltyRequest() {
		return fromLoyaltyRequest;
	}

	public void setFromLoyaltyRequest(boolean fromLoyaltyRequest) {
		this.fromLoyaltyRequest = fromLoyaltyRequest;
	}

	@Override
	public int hashCode() {
		return Objects.hash(couponCode);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CustomerCouponDTO other = (CustomerCouponDTO) obj;
		return Objects.equals(couponCode, other.couponCode);
	}

}
