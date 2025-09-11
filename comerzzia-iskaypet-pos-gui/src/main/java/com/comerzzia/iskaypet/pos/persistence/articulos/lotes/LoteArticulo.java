package com.comerzzia.iskaypet.pos.persistence.articulos.lotes;

import java.math.BigDecimal;
import java.util.Date;

public class LoteArticulo extends LoteArticuloKey {
    private String batch;

    private Date expirationDate;

    private BigDecimal stock;

    private Long version;

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch == null ? null : batch.trim();
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public BigDecimal getStock() {
        return stock;
    }

    public void setStock(BigDecimal stock) {
        this.stock = stock;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}