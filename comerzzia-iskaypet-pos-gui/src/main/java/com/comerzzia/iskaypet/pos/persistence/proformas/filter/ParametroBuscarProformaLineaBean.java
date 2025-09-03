package com.comerzzia.iskaypet.pos.persistence.proformas.filter;

import com.comerzzia.core.util.base.ParametrosBuscarBean;

import java.math.BigDecimal;
import java.util.Date;

public class ParametroBuscarProformaLineaBean extends ParametrosBuscarBean {

    private String uidActividad;

    private String idProforma;

    private Integer linea;

    private String codart;

    private Integer unidades;

    private BigDecimal cantidadConvertida;

    private BigDecimal cantidadSuministrada;

    private String unidadMedidaSuministrada;

    private String descuento;

    private BigDecimal importe;

    private String lote;

    private Date fechaCaducidad;

    public ParametroBuscarProformaLineaBean() {
    }

    public ParametroBuscarProformaLineaBean(String uidActividad, String idProforma, Integer linea, String codart, Integer unidades, BigDecimal cantidadConvertida, BigDecimal cantidadSuministrada, String unidadMedidaSuministrada, String descuento, BigDecimal importe, String lote, Date fechaCaducidad) {
        this.uidActividad = uidActividad;
        this.idProforma = idProforma;
        this.linea = linea;
        this.codart = codart;
        this.unidades = unidades;
        this.cantidadConvertida = cantidadConvertida;
        this.cantidadSuministrada = cantidadSuministrada;
        this.unidadMedidaSuministrada = unidadMedidaSuministrada;
        this.descuento = descuento;
        this.importe = importe;
        this.lote = lote;
        this.fechaCaducidad = fechaCaducidad;
    }

    public String getUidActividad() {
        return uidActividad;
    }

    public void setUidActividad(String uidActividad) {
        this.uidActividad = uidActividad;
    }

    public String getIdProforma() {
        return idProforma;
    }

    public void setIdProforma(String idProforma) {
        this.idProforma = idProforma;
    }

    public Integer getLinea() {
        return linea;
    }

    public void setLinea(Integer linea) {
        this.linea = linea;
    }

    public String getCodart() {
        return codart;
    }

    public void setCodart(String codart) {
        this.codart = codart;
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
        this.unidadMedidaSuministrada = unidadMedidaSuministrada;
    }

    public String getDescuento() {
        return descuento;
    }

    public void setDescuento(String descuento) {
        this.descuento = descuento;
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
        this.lote = lote;
    }

    public Date getFechaCaducidad() {
        return fechaCaducidad;
    }

    public void setFechaCaducidad(Date fechaCaducidad) {
        this.fechaCaducidad = fechaCaducidad;
    }
}
