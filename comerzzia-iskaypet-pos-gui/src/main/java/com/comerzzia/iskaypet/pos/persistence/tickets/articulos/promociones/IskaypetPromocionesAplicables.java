package com.comerzzia.iskaypet.pos.persistence.tickets.articulos.promociones;

import java.util.Date;

public class IskaypetPromocionesAplicables extends IskaypetPromocionesAplicablesKey {
    private String codtar;

    private String descripcion;

    private Date fechaInicio;

    private Date fechaFin;

    private String soloFidelizacion;

    private Long idTipoPromocion;

    private Long versionTarifa;

    private Long tipoDto;

    private String codColectivo;

    private String exclusiva;

    private String codCupon;

    private String aplicaATarifas;

    private String codtarPrecios;

    private byte[] datosPromocion;

    public String getCodtar() {
        return codtar;
    }

    public void setCodtar(String codtar) {
        this.codtar = codtar == null ? null : codtar.trim();
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion == null ? null : descripcion.trim();
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getSoloFidelizacion() {
        return soloFidelizacion;
    }

    public void setSoloFidelizacion(String soloFidelizacion) {
        this.soloFidelizacion = soloFidelizacion == null ? null : soloFidelizacion.trim();
    }

    public Long getIdTipoPromocion() {
        return idTipoPromocion;
    }

    public void setIdTipoPromocion(Long idTipoPromocion) {
        this.idTipoPromocion = idTipoPromocion;
    }

    public Long getVersionTarifa() {
        return versionTarifa;
    }

    public void setVersionTarifa(Long versionTarifa) {
        this.versionTarifa = versionTarifa;
    }

    public Long getTipoDto() {
        return tipoDto;
    }

    public void setTipoDto(Long tipoDto) {
        this.tipoDto = tipoDto;
    }

    public String getCodColectivo() {
        return codColectivo;
    }

    public void setCodColectivo(String codColectivo) {
        this.codColectivo = codColectivo == null ? null : codColectivo.trim();
    }

    public String getExclusiva() {
        return exclusiva;
    }

    public void setExclusiva(String exclusiva) {
        this.exclusiva = exclusiva == null ? null : exclusiva.trim();
    }

    public String getCodCupon() {
        return codCupon;
    }

    public void setCodCupon(String codCupon) {
        this.codCupon = codCupon == null ? null : codCupon.trim();
    }

    public String getAplicaATarifas() {
        return aplicaATarifas;
    }

    public void setAplicaATarifas(String aplicaATarifas) {
        this.aplicaATarifas = aplicaATarifas == null ? null : aplicaATarifas.trim();
    }

    public String getCodtarPrecios() {
        return codtarPrecios;
    }

    public void setCodtarPrecios(String codtarPrecios) {
        this.codtarPrecios = codtarPrecios == null ? null : codtarPrecios.trim();
    }

    public byte[] getDatosPromocion() {
        return datosPromocion;
    }

    public void setDatosPromocion(byte[] datosPromocion) {
        this.datosPromocion = datosPromocion;
    }
}