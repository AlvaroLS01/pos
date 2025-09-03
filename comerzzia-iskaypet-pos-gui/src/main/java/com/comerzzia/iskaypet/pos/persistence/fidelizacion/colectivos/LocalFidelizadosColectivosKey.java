package com.comerzzia.iskaypet.pos.persistence.fidelizacion.colectivos;

public class LocalFidelizadosColectivosKey {
    private String uidInstancia;

    private Long idFidelizado;

    private String codColectivo;

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

    public String getCodColectivo() {
        return codColectivo;
    }

    public void setCodColectivo(String codColectivo) {
        this.codColectivo = codColectivo == null ? null : codColectivo.trim();
    }
}