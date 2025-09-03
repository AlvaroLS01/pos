package com.comerzzia.iskaypet.pos.persistence.proformas.filter;

import com.comerzzia.core.util.base.ParametrosBuscarBean;

import java.math.BigDecimal;

public class ParametroBuscarProformaPagoBean extends ParametrosBuscarBean {

    private String uidActividad;

    private String idProforma;

    private Integer linea;

    private String codmedpag;

    private BigDecimal importePago;

    public ParametroBuscarProformaPagoBean() {
    }

    public ParametroBuscarProformaPagoBean(String uidActividad, String idProforma, Integer linea, String codmedpag, BigDecimal importePago) {
        this.uidActividad = uidActividad;
        this.idProforma = idProforma;
        this.linea = linea;
        this.codmedpag = codmedpag;
        this.importePago = importePago;
    }

    public String getUidActividad() {
        return uidActividad;
    }

    public void setUidActividad(String uidActividad) {
        this.uidActividad = uidActividad;
    }

    public String getIdProforma() {
        return idProforma;
    }

    public void setIdProforma(String idProforma) {
        this.idProforma = idProforma;
    }

    public Integer getLinea() {
        return linea;
    }

    public void setLinea(Integer linea) {
        this.linea = linea;
    }

    public String getCodmedpag() {
        return codmedpag;
    }

    public void setCodmedpag(String codmedpag) {
        this.codmedpag = codmedpag;
    }

    public BigDecimal getImportePago() {
        return importePago;
    }

    public void setImportePago(BigDecimal importePago) {
        this.importePago = importePago;
    }
}
