package com.comerzzia.iskaypet.pos.persistence.proformas.filter;

import com.comerzzia.core.util.base.ParametrosBuscarBean;

public class ParametroBuscarAuditoriaLineaProformaBean extends ParametrosBuscarBean {

    private String uidActividad;

    private String idProforma;

    private Integer linea;

    private String uidAuditoria;

    private String codemp;

    private String tipoAuditoria;

    private Long codMotivo;

    private String observaciones;

    public ParametroBuscarAuditoriaLineaProformaBean() {
    }

    public ParametroBuscarAuditoriaLineaProformaBean(String uidActividad, String idProforma, Integer linea, String uidAuditoria, String codemp, String tipoAuditoria, Long codMotivo, String observaciones) {
        this.uidActividad = uidActividad;
        this.idProforma = idProforma;
        this.linea = linea;
        this.uidAuditoria = uidAuditoria;
        this.codemp = codemp;
        this.tipoAuditoria = tipoAuditoria;
        this.codMotivo = codMotivo;
        this.observaciones = observaciones;
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

    public String getUidAuditoria() {
        return uidAuditoria;
    }

    public void setUidAuditoria(String uidAuditoria) {
        this.uidAuditoria = uidAuditoria;
    }

    public String getCodemp() {
        return codemp;
    }

    public void setCodemp(String codemp) {
        this.codemp = codemp;
    }

    public String getTipoAuditoria() {
        return tipoAuditoria;
    }

    public void setTipoAuditoria(String tipoAuditoria) {
        this.tipoAuditoria = tipoAuditoria;
    }

    public Long getCodMotivo() {
        return codMotivo;
    }

    public void setCodMotivo(Long codMotivo) {
        this.codMotivo = codMotivo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
