package com.comerzzia.iskaypet.pos.persistence.proformas.pagos;

import java.math.BigDecimal;

public class ProformaPagoBean extends ProformaPagoBeanKey {
    private String codmedpag;

    private BigDecimal importePago;

    public String getCodmedpag() {
        return codmedpag;
    }

    public void setCodmedpag(String codmedpag) {
        this.codmedpag = codmedpag == null ? null : codmedpag.trim();
    }

    public BigDecimal getImportePago() {
        return importePago;
    }

    public void setImportePago(BigDecimal importePago) {
        this.importePago = importePago;
    }
}