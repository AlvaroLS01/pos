package com.comerzzia.iskaypet.pos.persistence.ticket.gestion.documentos.ticket.contratos;

import java.util.Date;

public class POSTicketContrato {
    private String uidActividad;

    private String uidTicket;

    private String codalm;

    private Long idTicket;

    private Date fecha;

    private String procesado;

    private Date fechaProceso;

    private String mensajeProceso;

    private String codcaja;

    private Long idTipoDocumento;

    private String codTicket;

    private String firma;

    private String serieTicket;

    private String locatorId;

    private String contrato;

    public String getUidActividad() {
        return uidActividad;
    }

    public void setUidActividad(String uidActividad) {
        this.uidActividad = uidActividad == null ? null : uidActividad.trim();
    }

    public String getUidTicket() {
        return uidTicket;
    }

    public void setUidTicket(String uidTicket) {
        this.uidTicket = uidTicket == null ? null : uidTicket.trim();
    }

    public String getCodalm() {
        return codalm;
    }

    public void setCodalm(String codalm) {
        this.codalm = codalm == null ? null : codalm.trim();
    }

    public Long getIdTicket() {
        return idTicket;
    }

    public void setIdTicket(Long idTicket) {
        this.idTicket = idTicket;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getProcesado() {
        return procesado;
    }

    public void setProcesado(String procesado) {
        this.procesado = procesado == null ? null : procesado.trim();
    }

    public Date getFechaProceso() {
        return fechaProceso;
    }

    public void setFechaProceso(Date fechaProceso) {
        this.fechaProceso = fechaProceso;
    }

    public String getMensajeProceso() {
        return mensajeProceso;
    }

    public void setMensajeProceso(String mensajeProceso) {
        this.mensajeProceso = mensajeProceso == null ? null : mensajeProceso.trim();
    }

    public String getCodcaja() {
        return codcaja;
    }

    public void setCodcaja(String codcaja) {
        this.codcaja = codcaja == null ? null : codcaja.trim();
    }

    public Long getIdTipoDocumento() {
        return idTipoDocumento;
    }

    public void setIdTipoDocumento(Long idTipoDocumento) {
        this.idTipoDocumento = idTipoDocumento;
    }

    public String getCodTicket() {
        return codTicket;
    }

    public void setCodTicket(String codTicket) {
        this.codTicket = codTicket == null ? null : codTicket.trim();
    }

    public String getFirma() {
        return firma;
    }

    public void setFirma(String firma) {
        this.firma = firma == null ? null : firma.trim();
    }

    public String getSerieTicket() {
        return serieTicket;
    }

    public void setSerieTicket(String serieTicket) {
        this.serieTicket = serieTicket == null ? null : serieTicket.trim();
    }

    public String getLocatorId() {
        return locatorId;
    }

    public void setLocatorId(String locatorId) {
        this.locatorId = locatorId == null ? null : locatorId.trim();
    }

    public String getContrato() {
        return contrato;
    }

    public void setContrato(String contrato) {
        this.contrato = contrato == null ? null : contrato.trim();
    }
}