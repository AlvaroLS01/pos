package com.comerzzia.api.loyalty.service.accounts.dto;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlTransient;

import com.comerzzia.api.loyalty.service.coupons.dto.NewCoupon;

public class BalanceToCouponRequestDTO {
   @NotNull
   protected Long cardId;
   
   @XmlTransient
   protected Long accountId;
   
   @NotNull
   protected BigDecimal amount;
   
   protected Long userId;
   
   @NotNull
   protected NewCoupon newCoupon;

   public Long getCardId() {
      return cardId;
   }

   public void setCardId(Long cardId) {
      this.cardId = cardId;
   }

   public Long getAccountId() {
      return accountId;
   }

   public void setAccountId(Long accountId) {
      this.accountId = accountId;
   }

   public BigDecimal getAmount() {
      return amount;
   }

   public void setAmount(BigDecimal amount) {
      this.amount = amount;
   }

   public Long getUserId() {
      return userId;
   }

   public void setUserId(Long userId) {
      this.userId = userId;
   }

   public NewCoupon getNewCoupon() {
      return newCoupon;
   }

   public void setNewCoupon(NewCoupon newCoupon) {
      this.newCoupon = newCoupon;
   }
   
   
}
