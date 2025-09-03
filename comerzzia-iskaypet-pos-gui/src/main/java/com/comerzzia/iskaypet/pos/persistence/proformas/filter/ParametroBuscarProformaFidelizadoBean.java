package com.comerzzia.iskaypet.pos.persistence.proformas.filter;

import com.comerzzia.core.util.base.ParametrosBuscarBean;

public class ParametroBuscarProformaFidelizadoBean extends ParametrosBuscarBean {

    private String uidActividad;

    private String idProforma;

    private String uidInstancia;

    private Long idFidelizado;

    public ParametroBuscarProformaFidelizadoBean() {
    }

    public ParametroBuscarProformaFidelizadoBean(String uidActividad, String idProforma, String uidInstancia, Long idFidelizado) {
        this.uidActividad = uidActividad;
        this.idProforma = idProforma;
        this.uidInstancia = uidInstancia;
        this.idFidelizado = idFidelizado;
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

    public String getUidInstancia() {
        return uidInstancia;
    }

    public void setUidInstancia(String uidInstancia) {
        this.uidInstancia = uidInstancia;
    }

    public Long getIdFidelizado() {
        return idFidelizado;
    }

    public void setIdFidelizado(Long idFidelizado) {
        this.idFidelizado = idFidelizado;
    }
}
