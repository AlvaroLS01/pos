package com.comerzzia.api.loyalty.persistence.coupons.dto;

import java.math.BigDecimal;
import java.util.Date;

public class CouponsKpiDTO {
   protected Long issued;
   protected BigDecimal issuedAmount;
   protected Long valid;
   protected BigDecimal validAmount;
   protected Long redeemed;
   protected BigDecimal redeemedAmount;
   protected Long expired;
   protected BigDecimal expiredAmount;
   protected BigDecimal totalSale;
   protected BigDecimal totalDiscount;
   protected Long totalCouponsWithBalance;
   protected Long totalCustomers;   
   protected Date lastUse;
   
   public Long getIssued() {
      return issued;
   }
   public void setIssued(Long issued) {
      this.issued = issued;
   }
   public BigDecimal getIssuedAmount() {
      return issuedAmount;
   }
   public void setIssuedAmount(BigDecimal issuedAmount) {
      this.issuedAmount = issuedAmount;
   }
   public Long getValid() {
	   return valid;
   }
   public void setValid(Long valid) {
	   this.valid = valid;
   }
   public BigDecimal getValidAmount() {
	   return validAmount;
   }
   public void setValidAmount(BigDecimal validAmount) {
	   this.validAmount = validAmount;
   }
   public Long getRedeemed() {
      return redeemed;
   }
   public void setRedeemed(Long redeemed) {
      this.redeemed = redeemed;
   }
   public BigDecimal getRedeemedAmount() {
      return redeemedAmount;
   }
   public void setRedeemedAmount(BigDecimal redeemedAmount) {
      this.redeemedAmount = redeemedAmount;
   }
   public Long getExpired() {
      return expired;
   }
   public void setExpired(Long expired) {
      this.expired = expired;
   }
   public BigDecimal getExpiredAmount() {
      return expiredAmount;
   }
   public void setExpiredAmount(BigDecimal expiredAmount) {
      this.expiredAmount = expiredAmount;
   }
   public BigDecimal getTotalSale() {
      return totalSale;
   }
   public void setTotalSale(BigDecimal totalSale) {
      this.totalSale = totalSale;
   }
   public BigDecimal getTotalDiscount() {
      return totalDiscount;
   }
   public void setTotalDiscount(BigDecimal totalDiscount) {
      this.totalDiscount = totalDiscount;
   }
   public Long getTotalCouponsWithBalance() {
      return totalCouponsWithBalance;
   }
   public void setTotalCouponsWithBalance(Long totalCouponsWithBalance) {
      this.totalCouponsWithBalance = totalCouponsWithBalance;
   }
   public Long getTotalCustomers() {
      return totalCustomers;
   }
   public void setTotalCustomers(Long totalCustomers) {
      this.totalCustomers = totalCustomers;
   }
   public Date getLastUse() {
      return lastUse;
   }
   public void setLastUse(Date lastUse) {
      this.lastUse = lastUse;
   }
}
