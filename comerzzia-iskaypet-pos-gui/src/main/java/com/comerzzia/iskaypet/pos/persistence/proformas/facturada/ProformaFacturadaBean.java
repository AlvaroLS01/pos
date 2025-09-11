package com.comerzzia.iskaypet.pos.persistence.proformas.facturada;

import java.util.Date;

public class ProformaFacturadaBean extends ProformaFacturadaBeanKey {
    private Date fechaFacturacion;

    public Date getFechaFacturacion() {
        return fechaFacturacion;
    }

    public void setFechaFacturacion(Date fechaFacturacion) {
        this.fechaFacturacion = fechaFacturacion;
    }
}