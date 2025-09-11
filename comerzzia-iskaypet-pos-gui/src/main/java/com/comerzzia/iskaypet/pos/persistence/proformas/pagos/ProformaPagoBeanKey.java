package com.comerzzia.iskaypet.pos.persistence.proformas.pagos;

public class ProformaPagoBeanKey {
    private String uidActividad;

    private String idProforma;

    private Integer linea;

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

    public Integer getLinea() {
        return linea;
    }

    public void setLinea(Integer linea) {
        this.linea = linea;
    }
}