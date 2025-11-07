package com.comerzzia.api.loyalty.persistence.triggers.dto;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlTransient;

public class TriggerData {
   @XmlTransient
   protected String uidActividad;
   @XmlTransient
   protected String uidInstancia;
   
   protected String storeId;
   protected String salesChannelId;
   
   protected Date startPurchaseDate;
   protected Date endPurchaseDate;
   protected Integer startPurchaseDays;
   protected Integer endPurchaseDays;

   protected BigDecimal purchaseAmount;
   protected Integer purchaseCount;
   protected Integer unitsCount;
   
   protected byte[] catalogFilters;   
   protected byte[] customerFilters;
   
   protected String sql;

   public String getUidActividad() {
      return uidActividad;
   }

   public void setUidActividad(String uidActividad) {
      this.uidActividad = uidActividad;
   }

   public String getUidInstancia() {
      return uidInstancia;
   }

   public void setUidInstancia(String uidInstancia) {
      this.uidInstancia = uidInstancia;
   }

   public String getStoreId() {
      return storeId;
   }

   public void setStoreId(String storeId) {
      this.storeId = storeId;
   }

   public String getSalesChannelId() {
      return salesChannelId;
   }

   public void setSalesChannelId(String salesChannelId) {
      this.salesChannelId = salesChannelId;
   }
   
   public Date getStartPurchaseDate() {
      return startPurchaseDate;
   }

   public void setStartPurchaseDate(Date startPurchaseDate) {
      this.startPurchaseDate = startPurchaseDate;
   }

   public Date getEndPurchaseDate() {
      return endPurchaseDate;
   }

   public void setEndPurchaseDate(Date endPurchaseDate) {
      this.endPurchaseDate = endPurchaseDate;
   }

   public Integer getStartPurchaseDays() {
      return startPurchaseDays;
   }

   public void setStartPurchaseDays(Integer startPurchaseDays) {
      this.startPurchaseDays = startPurchaseDays;
   }

   public Integer getEndPurchaseDays() {
      return endPurchaseDays;
   }

   public void setEndPurchaseDays(Integer endPurchaseDays) {
      this.endPurchaseDays = endPurchaseDays;
   }

   public BigDecimal getPurchaseAmount() {
      return purchaseAmount;
   }

   public void setPurchaseAmount(BigDecimal purchaseAmount) {
      this.purchaseAmount = purchaseAmount;
   }

   public Integer getPurchaseCount() {
      return purchaseCount;
   }

   public void setPurchaseCount(Integer purchaseCount) {
      this.purchaseCount = purchaseCount;
   }

   public Integer getUnitsCount() {
      return unitsCount;
   }

   public void setUnitsCount(Integer unitsCount) {
      this.unitsCount = unitsCount;
   }

   public byte[] getCatalogFilters() {
      return catalogFilters;
   }

   public void setCatalogFilters(byte[] catalogFilters) {
      this.catalogFilters = catalogFilters;
   }

   public byte[] getCustomerFilters() {
      return customerFilters;
   }

   public void setCustomerFilters(byte[] customerFilters) {
      this.customerFilters = customerFilters;
   }

   public String getSql() {
      return sql;
   }

   public void setSql(String sql) {
      this.sql = sql;
   }
}
