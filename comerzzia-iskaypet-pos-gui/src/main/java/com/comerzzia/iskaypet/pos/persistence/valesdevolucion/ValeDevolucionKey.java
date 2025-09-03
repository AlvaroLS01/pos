package com.comerzzia.iskaypet.pos.persistence.valesdevolucion;

public class ValeDevolucionKey {

    private String uidInstancia;
    private String numeroTarjeta;

    public ValeDevolucionKey() {
    }

    public ValeDevolucionKey(String uidInstancia, String numeroTarjeta) {
        this.uidInstancia = uidInstancia;
        this.numeroTarjeta = numeroTarjeta;
    }

    public ValeDevolucionKey(ValeDevolucionKey key) {
        this.uidInstancia = key.getUidInstancia();
        this.numeroTarjeta = key.getNumeroTarjeta();
    }

    public String getUidInstancia() {
        return uidInstancia;
    }

    public void setUidInstancia(String uidInstancia) {
        this.uidInstancia = uidInstancia == null ? null : uidInstancia.trim();
    }

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta == null ? null : numeroTarjeta.trim();
    }
}