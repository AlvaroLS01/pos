package com.comerzzia.iskaypet.pos.persistence.colectivos;

public class LocalColectivosKey {
    private String uidInstancia;

    private String codColectivo;

    public String getUidInstancia() {
        return uidInstancia;
    }

    public void setUidInstancia(String uidInstancia) {
        this.uidInstancia = uidInstancia == null ? null : uidInstancia.trim();
    }

    public String getCodColectivo() {
        return codColectivo;
    }

    public void setCodColectivo(String codColectivo) {
        this.codColectivo = codColectivo == null ? null : codColectivo.trim();
    }
}