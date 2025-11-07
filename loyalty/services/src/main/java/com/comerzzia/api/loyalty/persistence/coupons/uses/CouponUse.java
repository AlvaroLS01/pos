package com.comerzzia.api.loyalty.persistence.coupons.uses;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class CouponUse extends CouponUseKey {
    @XmlElement
    private Long maxUses;

    @XmlElement
    private Integer uses = 0;

    @XmlElement
    private Boolean used = false;

    @XmlElement
    private BigDecimal totalDiscount;

    @XmlElement
    private Date firstUse;

    @XmlElement
    private Date lastUse;

    @XmlElement
    private String lastTerminalId;

    @XmlElement
    private String lockByTerminalId;

    @XmlElement
    private Date lockDate;
    
    @XmlElement
    private BigDecimal totalSale;

    public Long getMaxUses() {
        return maxUses;
    }

    public void setMaxUses(Long maxUses) {
        this.maxUses = maxUses;
    }

    public Integer getUses() {
        return uses;
    }

    public void setUses(Integer uses) {
        this.uses = uses;
    }

    public Boolean getUsed() {
        return used;
    }

    public void setUsed(Boolean used) {
        this.used = used;
    }

    public BigDecimal getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(BigDecimal totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public Date getFirstUse() {
        return firstUse;
    }

    public void setFirstUse(Date firstUse) {
        this.firstUse = firstUse;
    }

    public Date getLastUse() {
        return lastUse;
    }

    public void setLastUse(Date lastUse) {
        this.lastUse = lastUse;
    }

    public String getLastTerminalId() {
        return lastTerminalId;
    }

    public void setLastTerminalId(String lastTerminalId) {
        this.lastTerminalId = lastTerminalId == null ? null : lastTerminalId.trim();
    }

    public String getLockByTerminalId() {
        return lockByTerminalId;
    }

    public void setLockByTerminalId(String lockByTerminalId) {
        this.lockByTerminalId = lockByTerminalId == null ? null : lockByTerminalId.trim();
    }

    public Date getLockDate() {
        return lockDate;
    }

    public void setLockDate(Date lockDate) {
        this.lockDate = lockDate;
    }

   public BigDecimal getTotalSale() {
      return totalSale;
   }

   public void setTotalSale(BigDecimal totalSale) {
      this.totalSale = totalSale;
   }
}