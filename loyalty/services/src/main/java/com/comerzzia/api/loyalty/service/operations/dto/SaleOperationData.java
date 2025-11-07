package com.comerzzia.api.loyalty.service.operations.dto;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class SaleOperationData {
   @NotNull
   protected String loyalCustomerId;
   @Size(max=40)
   @NotNull
   protected String ticketUid;
   @Size(max=4)
   @NotNull
   protected String storeId;
   protected String tillId;
   @NotNull
   protected String lockByTerminalId;
   @NotNull
   protected Date date;  
   
   public String getLoyalCustomerId() {
      return loyalCustomerId;
   }

   public void setLoyalCustomerId(String loyalCustomerId) {
      this.loyalCustomerId = loyalCustomerId;
   }

   public String getTicketUid() {
      return ticketUid;
   }

   public void setTicketUid(String ticketUid) {
      this.ticketUid = ticketUid;
   }

   public String getStoreId() {
      return storeId;
   }

   public void setStoreId(String storeId) {
      this.storeId = storeId;
   }

   public String getTillId() {
      return tillId;
   }

   public void setTillId(String tillId) {
      this.tillId = tillId;
   }

   public String getLockByTerminalId() {
      return lockByTerminalId;
   }

   public void setLockByTerminalId(String lockByTerminalId) {
      this.lockByTerminalId = lockByTerminalId;
   }

   public Date getDate() {
      return date;
   }

   public void setDate(Date date) {
      this.date = date;
   }

}
