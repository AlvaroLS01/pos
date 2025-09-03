package com.comerzzia.iskaypet.pos.persistence.colectivos;

public class LocalColectivos extends LocalColectivosKey {
    private String desColectivo;

    private String codtipcolectivo;

    private String destipcolectivo;

    public String getDesColectivo() {
        return desColectivo;
    }

    public void setDesColectivo(String desColectivo) {
        this.desColectivo = desColectivo == null ? null : desColectivo.trim();
    }

    public String getCodtipcolectivo() {
        return codtipcolectivo;
    }

    public void setCodtipcolectivo(String codtipcolectivo) {
        this.codtipcolectivo = codtipcolectivo == null ? null : codtipcolectivo.trim();
    }

    public String getDestipcolectivo() {
        return destipcolectivo;
    }

    public void setDestipcolectivo(String destipcolectivo) {
        this.destipcolectivo = destipcolectivo == null ? null : destipcolectivo.trim();
    }
}