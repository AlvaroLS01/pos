package com.comerzzia.dinosol.pos.persistence.motivos;

public class MotivosCambioPrecioKey {
    private String uidActividad;

    private String codMotivo;

    public String getUidActividad() {
        return uidActividad;
    }

    public void setUidActividad(String uidActividad) {
        this.uidActividad = uidActividad == null ? null : uidActividad.trim();
    }

    public String getCodMotivo() {
        return codMotivo;
    }

    public void setCodMotivo(String codMotivo) {
        this.codMotivo = codMotivo == null ? null : codMotivo.trim();
    }
}