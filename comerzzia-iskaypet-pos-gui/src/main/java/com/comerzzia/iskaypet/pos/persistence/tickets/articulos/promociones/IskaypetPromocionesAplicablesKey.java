package com.comerzzia.iskaypet.pos.persistence.tickets.articulos.promociones;

public class IskaypetPromocionesAplicablesKey {
    private String uidActividad;

    private Long idPromocion;

    public String getUidActividad() {
        return uidActividad;
    }

    public void setUidActividad(String uidActividad) {
        this.uidActividad = uidActividad == null ? null : uidActividad.trim();
    }

    public Long getIdPromocion() {
        return idPromocion;
    }

    public void setIdPromocion(Long idPromocion) {
        this.idPromocion = idPromocion;
    }
}