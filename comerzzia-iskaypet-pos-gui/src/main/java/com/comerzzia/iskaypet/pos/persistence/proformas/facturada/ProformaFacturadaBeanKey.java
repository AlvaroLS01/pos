package com.comerzzia.iskaypet.pos.persistence.proformas.facturada;

public class ProformaFacturadaBeanKey {
    private String uidActividad;

    private String idProforma;

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
}