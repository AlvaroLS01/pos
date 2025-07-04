package com.comerzzia.cardoso.pos.persistence.taxfree.movimientos;

import java.util.Date;

public class MovimientosTaxfree extends MovimientosTaxfreeKey {
    private Date fechaMovimiento;

    private String cajaMovimiento;

    private String pasaporte;

    public Date getFechaMovimiento() {
        return fechaMovimiento;
    }

    public void setFechaMovimiento(Date fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }

    public String getCajaMovimiento() {
        return cajaMovimiento;
    }

    public void setCajaMovimiento(String cajaMovimiento) {
        this.cajaMovimiento = cajaMovimiento == null ? null : cajaMovimiento.trim();
    }

    public String getPasaporte() {
        return pasaporte;
    }

    public void setPasaporte(String pasaporte) {
        this.pasaporte = pasaporte == null ? null : pasaporte.trim();
    }
}