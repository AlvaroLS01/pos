package com.comerzzia.iskaypet.pos.persistence.ticket.contrato.trazabilidad.items.ident;

public class ItemsPetsIdentKey {
    private String uidActividad;

    private String codalm;

    private String codart;

    private String desglose1;

    private String desglose2;

    private Integer linea;

    public String getUidActividad() {
        return uidActividad;
    }

    public void setUidActividad(String uidActividad) {
        this.uidActividad = uidActividad == null ? null : uidActividad.trim();
    }

    public String getCodalm() {
        return codalm;
    }

    public void setCodalm(String codalm) {
        this.codalm = codalm == null ? null : codalm.trim();
    }

    public String getCodart() {
        return codart;
    }

    public void setCodart(String codart) {
        this.codart = codart == null ? null : codart.trim();
    }

    public String getDesglose1() {
        return desglose1;
    }

    public void setDesglose1(String desglose1) {
        this.desglose1 = desglose1 == null ? null : desglose1.trim();
    }

    public String getDesglose2() {
        return desglose2;
    }

    public void setDesglose2(String desglose2) {
        this.desglose2 = desglose2 == null ? null : desglose2.trim();
    }

    public Integer getLinea() {
        return linea;
    }

    public void setLinea(Integer linea) {
        this.linea = linea;
    }
}