package com.comerzzia.cardoso.pos.persistence.taxfree.movimientos;

public class MovimientosTaxfreeKey {
    private String uidActividad;

    private String codTicket;

    private String barcode;

    private String tipoMovimiento;

    public String getUidActividad() {
        return uidActividad;
    }

    public void setUidActividad(String uidActividad) {
        this.uidActividad = uidActividad == null ? null : uidActividad.trim();
    }

    public String getCodTicket() {
        return codTicket;
    }

    public void setCodTicket(String codTicket) {
        this.codTicket = codTicket == null ? null : codTicket.trim();
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode == null ? null : barcode.trim();
    }

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento == null ? null : tipoMovimiento.trim();
    }
}