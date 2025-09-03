package com.comerzzia.iskaypet.pos.persistence.proformas;

import java.util.Date;

public class ProformaBean extends ProformaBeanKey {

    public static String ESTADO_FACTURADA = "FACTURADA";
    public static String ESTADO_ENVIADA = "ENVIADA";
    public static String ESTADO_NO_FACTURADA = "NO FACTURADA";
    public static String ESTADO_ERROR = "ERROR";

    private String sistemaOrigen;

    private Date fecha;

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

    public String getSistemaOrigen() {
        return sistemaOrigen;
    }

    public void setSistemaOrigen(String sistemaOrigen) {
        this.sistemaOrigen = sistemaOrigen == null ? null : sistemaOrigen.trim();
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
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
        this.codalm = codalm == null ? null : codalm.trim();
    }

    public String getEstadoActual() {
        return estadoActual;
    }

    public void setEstadoActual(String estadoActual) {
        this.estadoActual = estadoActual == null ? null : estadoActual.trim();
    }

    public String getUidTicketOrigen() {
        return uidTicketOrigen;
    }

    public void setUidTicketOrigen(String uidTicketOrigen) {
        this.uidTicketOrigen = uidTicketOrigen == null ? null : uidTicketOrigen.trim();
    }

    public String getSerieOrigen() {
        return serieOrigen;
    }

    public void setSerieOrigen(String serieOrigen) {
        this.serieOrigen = serieOrigen == null ? null : serieOrigen.trim();
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
        this.codalmOrigen = codalmOrigen == null ? null : codalmOrigen.trim();
    }

    public String getCajaOrigen() {
        return cajaOrigen;
    }

    public void setCajaOrigen(String cajaOrigen) {
        this.cajaOrigen = cajaOrigen == null ? null : cajaOrigen.trim();
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
        this.codigoFacturaOrigen = codigoFacturaOrigen == null ? null : codigoFacturaOrigen.trim();
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