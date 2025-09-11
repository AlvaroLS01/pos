package com.comerzzia.iskaypet.pos.services.proformas.rest.classes.pagos;


import java.math.BigDecimal;

public class PagoProformaDTO {

    private String medioPago;

    private BigDecimal importePago;

    public PagoProformaDTO() {
    }

    public PagoProformaDTO(String medioPago, BigDecimal importePago) {
        this.medioPago = medioPago;
        this.importePago = importePago;
    }

    public String getMedioPago() {
        return medioPago;
    }

    public void setMedioPago(String medioPago) {
        this.medioPago = medioPago;
    }

    public BigDecimal getImportePago() {
        return importePago;
    }

    public void setImportePago(BigDecimal importePago) {
        this.importePago = importePago;
    }
}
