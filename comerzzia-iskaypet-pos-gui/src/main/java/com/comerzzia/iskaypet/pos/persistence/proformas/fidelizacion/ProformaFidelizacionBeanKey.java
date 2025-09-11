package com.comerzzia.iskaypet.pos.persistence.proformas.fidelizacion;

public class ProformaFidelizacionBeanKey {
    private String uidActividad;

    private String idProforma;

    private String uidInstancia;

    private Long idFidelizado;

    public String getUidActividad() {
        return uidActividad;
    }

    public void setUidActividad(String uidActividad) {
        this.uidActividad = uidActividad == null ? null : uidActividad.trim();
    }

    public String getIdProforma() {
        return idProforma;
    }

    public void setIdProforma(String idProforma) {
        this.idProforma = idProforma == null ? null : idProforma.trim();
    }

    public String getUidInstancia() {
        return uidInstancia;
    }

    public void setUidInstancia(String uidInstancia) {
        this.uidInstancia = uidInstancia == null ? null : uidInstancia.trim();
    }

    public Long getIdFidelizado() {
        return idFidelizado;
    }

    public void setIdFidelizado(Long idFidelizado) {
        this.idFidelizado = idFidelizado;
    }
}