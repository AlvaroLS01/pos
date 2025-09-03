package com.comerzzia.iskaypet.pos.services.ticket.lineas.inyectables;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.math.BigDecimal;

@XmlAccessorType(XmlAccessType.FIELD)
public class Inyectable {

    @XmlElement(name = "cantidad_convertida")
    private BigDecimal cantidadConvertida;

    @XmlElement(name = "cantidad_suministrada")
    private BigDecimal cantidadSuministrada;

    @XmlElement(name = "unidad_medida_suministrada")
    private String unidadMedidaSuministrada;

    public Inyectable() {
    }

    public Inyectable(BigDecimal cantidadConvertida, BigDecimal cantidadSuministrada, String unidadMedidaSuministrada) {
        this.cantidadConvertida = cantidadConvertida;
        this.cantidadSuministrada = cantidadSuministrada;
        this.unidadMedidaSuministrada = unidadMedidaSuministrada;
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
}
