package com.comerzzia.iskaypet.pos.persistence.proformas.filter;

import com.comerzzia.core.util.base.ParametrosBuscarBean;

import java.util.Date;

public class ParametroBuscarProformaBean extends ParametrosBuscarBean {

    private String uidActividad;

    private String idProforma;

    private String sistemaOrigen;

    private Date fecha;

    private Date fechaDesde;

    private Date fechaDesdeOIgual;

    private  Date fechaHasta;

    private  Date fechaHastaOIgual;

    private Long idTipoDocumento;

    private Boolean automatica;

    private String codalm;

    private String estadoActual;

    private String uidTicketOrigen;

    private String serieOrigen;

    private Long numalbOrigen;

    private String codalmOrigen;

    private String cajaOrigen;

    private Long tipoDocumentoOrigen;

    private String codigoFacturaOrigen;

    private Date fechaCreacion;

    private Date fechaModificacion;

    public ParametroBuscarProformaBean() {
    }

    public ParametroBuscarProformaBean(String uidActividad, String idProforma, String sistemaOrigen, Date fecha, Date fechaDesde, Date fechaDesdeOIgual, Date fechaHasta, Date fechaHastaOIgual, Long idTipoDocumento, Boolean automatica, String codalm, String estadoActual, String uidTicketOrigen, String serieOrigen, Long numalbOrigen, String codalmOrigen, String cajaOrigen, Long tipoDocumentoOrigen, String codigoFacturaOrigen, Date fechaCreacion, Date fechaModificacion) {
        this.uidActividad = uidActividad;
        this.idProforma = idProforma;
        this.sistemaOrigen = sistemaOrigen;
        this.fecha = fecha;
        this.fechaDesde = fechaDesde;
        this.fechaDesdeOIgual = fechaDesdeOIgual;
        this.fechaHasta = fechaHasta;
        this.fechaHastaOIgual = fechaHastaOIgual;
        this.idTipoDocumento = idTipoDocumento;
        this.automatica = automatica;
        this.codalm = codalm;
        this.estadoActual = estadoActual;
        this.uidTicketOrigen = uidTicketOrigen;
        this.serieOrigen = serieOrigen;
        this.numalbOrigen = numalbOrigen;
        this.codalmOrigen = codalmOrigen;
        this.cajaOrigen = cajaOrigen;
        this.tipoDocumentoOrigen = tipoDocumentoOrigen;
        this.codigoFacturaOrigen = codigoFacturaOrigen;
        this.fechaCreacion = fechaCreacion;
        this.fechaModificacion = fechaModificacion;
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

    public String getSistemaOrigen() {
        return sistemaOrigen;
    }

    public void setSistemaOrigen(String sistemaOrigen) {
        this.sistemaOrigen = sistemaOrigen;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Date getFechaDesde() {
        return fechaDesde;
    }

    public void setFechaDesde(Date fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    public Date getFechaDesdeOIgual() {
        return fechaDesdeOIgual;
    }

    public void setFechaDesdeOIgual(Date fechaDesdeOIgual) {
        this.fechaDesdeOIgual = fechaDesdeOIgual;
    }

    public Date getFechaHasta() {
        return fechaHasta;
    }

    public void setFechaHasta(Date fechaHasta) {
        this.fechaHasta = fechaHasta;
    }

    public Date getFechaHastaOIgual() {
        return fechaHastaOIgual;
    }

    public void setFechaHastaOIgual(Date fechaHastaOIgual) {
        this.fechaHastaOIgual = fechaHastaOIgual;
    }

    public Long getIdTipoDocumento() {
        return idTipoDocumento;
    }

    public void setIdTipoDocumento(Long idTipoDocumento) {
        this.idTipoDocumento = idTipoDocumento;
    }

    public Boolean getAutomatica() {
        return automatica;
    }

    public void setAutomatica(Boolean automatica) {
        this.automatica = automatica;
    }

    public String getCodalm() {
        return codalm;
    }

    public void setCodalm(String codalm) {
        this.codalm = codalm;
    }

    public String getEstadoActual() {
        return estadoActual;
    }

    public void setEstadoActual(String estadoActual) {
        this.estadoActual = estadoActual;
    }

    public String getUidTicketOrigen() {
        return uidTicketOrigen;
    }

    public void setUidTicketOrigen(String uidTicketOrigen) {
        this.uidTicketOrigen = uidTicketOrigen;
    }

    public String getSerieOrigen() {
        return serieOrigen;
    }

    public void setSerieOrigen(String serieOrigen) {
        this.serieOrigen = serieOrigen;
    }

    public Long getNumalbOrigen() {
        return numalbOrigen;
    }

    public void setNumalbOrigen(Long numalbOrigen) {
        this.numalbOrigen = numalbOrigen;
    }

    public String getCodalmOrigen() {
        return codalmOrigen;
    }

    public void setCodalmOrigen(String codalmOrigen) {
        this.codalmOrigen = codalmOrigen;
    }

    public String getCajaOrigen() {
        return cajaOrigen;
    }

    public void setCajaOrigen(String cajaOrigen) {
        this.cajaOrigen = cajaOrigen;
    }

    public Long getTipoDocumentoOrigen() {
        return tipoDocumentoOrigen;
    }

    public void setTipoDocumentoOrigen(Long tipoDocumentoOrigen) {
        this.tipoDocumentoOrigen = tipoDocumentoOrigen;
    }

    public String getCodigoFacturaOrigen() {
        return codigoFacturaOrigen;
    }

    public void setCodigoFacturaOrigen(String codigoFacturaOrigen) {
        this.codigoFacturaOrigen = codigoFacturaOrigen;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Date getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(Date fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }
}
