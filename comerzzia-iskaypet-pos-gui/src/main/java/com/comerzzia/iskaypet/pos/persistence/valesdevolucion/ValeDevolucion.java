package com.comerzzia.iskaypet.pos.persistence.valesdevolucion;

import java.math.BigDecimal;
import java.util.Date;

public class ValeDevolucion extends ValeDevolucionKey {

    private BigDecimal saldoDisponible;
    private Date fechaActivacion;

    public ValeDevolucion() {
    }

    public ValeDevolucion(ValeDevolucionKey key) {
        super(key);
    }

    public ValeDevolucion(ValeDevolucionKey key, BigDecimal saldoDisponible, Date fechaActivacion) {
        super(key);
        this.saldoDisponible = saldoDisponible;
        this.fechaActivacion = fechaActivacion;
    }

    public ValeDevolucion(String uidInstancia, String numeroTarjeta, BigDecimal saldoDisponible, Date fechaActivacion) {
        super(uidInstancia, numeroTarjeta);
        this.saldoDisponible = saldoDisponible;
        this.fechaActivacion = fechaActivacion;
    }

    public BigDecimal getSaldoDisponible() {
        return saldoDisponible;
    }

    public void setSaldoDisponible(BigDecimal saldoDisponible) {
        this.saldoDisponible = saldoDisponible;
    }

    public Date getFechaActivacion() {
        return fechaActivacion;
    }

    public void setFechaActivacion(Date fechaActivacion) {
        this.fechaActivacion = fechaActivacion;
    }
}