package com.comerzzia.iskaypet.pos.persistence.ticket.contrato.prefijos;

public class PrefijosPaisesKey {
    private String uidInstancia;

    private String codpais;

    private String prefijo;

    public String getUidInstancia() {
        return uidInstancia;
    }

    public void setUidInstancia(String uidInstancia) {
        this.uidInstancia = uidInstancia == null ? null : uidInstancia.trim();
    }

    public String getCodpais() {
        return codpais;
    }

    public void setCodpais(String codpais) {
        this.codpais = codpais == null ? null : codpais.trim();
    }

    public String getPrefijo() {
        return prefijo;
    }

    public void setPrefijo(String prefijo) {
        this.prefijo = prefijo == null ? null : prefijo.trim();
    }
}