package com.comerzzia.iskaypet.pos.persistence.proformas.lineas;

import java.math.BigDecimal;
import java.util.Date;

public class ProformaLineaBean extends ProformaLineaBeanKey {
    private String codart;

    private Integer unidades;

    private BigDecimal cantidadConvertida;

    private BigDecimal cantidadSuministrada;

    private String unidadMedidaSuministrada;

    private String descuento;

    private BigDecimal importe;

    private String lote;

    private Date fechaCaducidad;

    public String getCodart() {
        return codart;
    }

    public void setCodart(String codart) {
        this.codart = codart == null ? null : codart.trim();
    }

    public Integer getUnidades() {
        return unidades;
    }

    public void setUnidades(Integer unidades) {
        this.unidades = unidades;
    }

    public BigDecimal getCantidadConvertida() {
        return cantidadConvertida;
    }

    public void setCantidadConvertida(BigDecimal cantidadConvertida) {
        this.cantidadConvertida = cantidadConvertida;
    }

    public BigDecimal getCantidadSuministrada() {
        return cantidadSuministrada;
    }

    public void setCantidadSuministrada(BigDecimal cantidadSuministrada) {
        this.cantidadSuministrada = cantidadSuministrada;
    }

    public String getUnidadMedidaSuministrada() {
        return unidadMedidaSuministrada;
    }

    public void setUnidadMedidaSuministrada(String unidadMedidaSuministrada) {
        this.unidadMedidaSuministrada = unidadMedidaSuministrada == null ? null : unidadMedidaSuministrada.trim();
    }

    public String getDescuento() {
        return descuento;
    }

    public void setDescuento(String descuento) {
        this.descuento = descuento == null ? null : descuento.trim();
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote == null ? null : lote.trim();
    }

    public Date getFechaCaducidad() {
        return fechaCaducidad;
    }

    public void setFechaCaducidad(Date fechaCaducidad) {
        this.fechaCaducidad = fechaCaducidad;
    }
}