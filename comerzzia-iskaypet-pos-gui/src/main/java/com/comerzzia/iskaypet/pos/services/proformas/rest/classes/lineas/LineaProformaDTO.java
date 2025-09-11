package com.comerzzia.iskaypet.pos.services.proformas.rest.classes.lineas;


import com.comerzzia.iskaypet.pos.services.proformas.rest.classes.lineas.auditorias.AuditoriaLineaProformaDTO;

import java.math.BigDecimal;
import java.util.List;


public class LineaProformaDTO {

    private String articulo;
    private Integer unidades;
    private BigDecimal cantidadConvertida;
    private BigDecimal cantidadSuministrada;
    private String unidadMedidaSuministrada;
    private String lote;
    private String fechaCaducidad;
    private String descuento;
    private BigDecimal importe;
    private List<AuditoriaLineaProformaDTO> auditorias;

    public LineaProformaDTO() {
    }

    public LineaProformaDTO(String articulo, Integer unidades, BigDecimal cantidadConvertida, BigDecimal cantidadSuministrada, String unidadMedidaSuministrada, String lote, String fechaCaducidad, String descuento, BigDecimal importe, List<AuditoriaLineaProformaDTO> auditorias) {
        this.articulo = articulo;
        this.unidades = unidades;
        this.cantidadConvertida = cantidadConvertida;
        this.cantidadSuministrada = cantidadSuministrada;
        this.unidadMedidaSuministrada = unidadMedidaSuministrada;
        this.lote = lote;
        this.fechaCaducidad = fechaCaducidad;
        this.descuento = descuento;
        this.importe = importe;
        this.auditorias = auditorias;
    }

    public String getArticulo() {
        return articulo;
    }

    public void setArticulo(String articulo) {
        this.articulo = articulo;
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

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public String getFechaCaducidad() {
        return fechaCaducidad;
    }

    public void setFechaCaducidad(String fechaCaducidad) {
        this.fechaCaducidad = fechaCaducidad;
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

    public List<AuditoriaLineaProformaDTO> getAuditorias() {
        return auditorias;
    }

    public void setAuditorias(List<AuditoriaLineaProformaDTO> auditorias) {
        this.auditorias = auditorias;
    }
}
