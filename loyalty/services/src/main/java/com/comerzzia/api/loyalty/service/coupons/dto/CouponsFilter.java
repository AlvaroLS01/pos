package com.comerzzia.api.loyalty.service.coupons.dto;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ws.rs.QueryParam;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang.time.DateUtils;

import com.comerzzia.api.loyalty.persistence.coupons.links.CouponLinkKey;

public class CouponsFilter {
   @QueryParam("active") 
   protected Boolean active;
   @QueryParam("used") 
   protected Boolean used;
   @QueryParam("valid") 
   protected Boolean valid;
   @QueryParam("validInFuture") 
   protected Boolean validInFuture;
   //TODO: ¿ es necesario implementar ?
   @QueryParam("lockByTerminalId") 
   protected String lockByTerminalId;
   @QueryParam("customerId")
   protected String loyalCustomerId;
   @QueryParam("includeAnonymousCoupons")
   protected Boolean includeAnonymousCoupons;
   @QueryParam("manualSelection") 
   protected Boolean manualSelection;
   @QueryParam("languageCode")
   protected String languageCode;
   
   //TODO: No funciona como queryParam las listas de objectos. O se cambia a POST o se rodea el problema
   @QueryParam("links")
   private List<CouponLinkKey> links;
   
   // truncate validation date
   @XmlTransient
   protected Date validDate = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
   
   public Boolean getActive() {
      return active;
   }
   public void setActive(Boolean active) {
      this.active = active;
   }
   public Boolean getUsed() {
      return used;
   }
   public void setUsed(Boolean used) {
      this.used = used;
   }
   public Boolean getValid() {
      return valid;
   }
   public void setValid(Boolean valid) {
      this.valid = valid;
   }
   public Boolean getValidInFuture() {
      return validInFuture;
   }
   public void setValidInFuture(Boolean validInFuture) {
      this.validInFuture = validInFuture;
   }
   public String getLockByTerminalId() {
      return lockByTerminalId;
   }
   public void setLockByTerminalId(String lockByTerminalId) {
      this.lockByTerminalId = lockByTerminalId;
   }
   public String getLoyalCustomerId() {
      return loyalCustomerId;
   }
   public void setLoyalCustomerId(String loyalCustomerId) {
      this.loyalCustomerId = loyalCustomerId;
   }
   public Boolean getIncludeAnonymousCoupons() {
      return includeAnonymousCoupons;
   }
   public void setIncludeAnonymousCoupons(Boolean includeAnonymousCoupons) {
      this.includeAnonymousCoupons = includeAnonymousCoupons;
   }
   public Date getValidDate() {
      return validDate;
   }
   public void setValidDate(Date validDate) {
      this.validDate = validDate;
   }
   public List<CouponLinkKey> getLinks() {
      return links;
   }
   public void setLinks(List<CouponLinkKey> links) {
      this.links = links;
   }
   public Boolean getManualSelection() {
      return manualSelection;
   }
   public void setManualSelection(Boolean manualSelection) {
      this.manualSelection = manualSelection;
   }
   public String getLanguageCode() {
	  return languageCode;
   }
   public void setLanguageCode(String languageCode) {
	  this.languageCode = languageCode;
   }         
}
