package com.comerzzia.iskaypet.pos.services.proformas.rest.classes;


public class ProformaHeaderDTO {

    private String uidActividad;
    private String idProforma;
    private String sistemaOrigen;
    private String tipoDocumento;
    private String fechaProforma;
    private Long idAlbaran;
    private boolean automatica;
    private String idProformaOrigen;
    private String documentoOrigen;
    private String localizador;
    private String almacen;
    private String nombreCliente;
    private String estadoActual;
    private String fechaCreacion;
    private String fechaModificacion;

    public ProformaHeaderDTO() {
    }

    public ProformaHeaderDTO(String uidActividad, String idProforma, String sistemaOrigen, String tipoDocumento, String fechaProforma, Long idAlbaran, boolean automatica, String idProformaOrigen, String documentoOrigen, String localizador, String almacen, String nombreCliente, String estadoActual, String fechaCreacion, String fechaModificacion) {
        this.uidActividad = uidActividad;
        this.idProforma = idProforma;
        this.sistemaOrigen = sistemaOrigen;
        this.tipoDocumento = tipoDocumento;
        this.fechaProforma = fechaProforma;
        this.idAlbaran = idAlbaran;
        this.automatica = automatica;
        this.idProformaOrigen = idProformaOrigen;
        this.documentoOrigen = documentoOrigen;
        this.localizador = localizador;
        this.almacen = almacen;
        this.nombreCliente = nombreCliente;
        this.estadoActual = estadoActual;
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

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getFechaProforma() {
        return fechaProforma;
    }

    public void setFechaProforma(String fechaProforma) {
        this.fechaProforma = fechaProforma;
    }

    public Long getIdAlbaran() {
        return idAlbaran;
    }

    public void setIdAlbaran(Long idAlbaran) {
        this.idAlbaran = idAlbaran;
    }

    public boolean isAutomatica() {
        return automatica;
    }

    public void setAutomatica(boolean automatica) {
        this.automatica = automatica;
    }

    public String getIdProformaOrigen() {
        return idProformaOrigen;
    }

    public void setIdProformaOrigen(String idProformaOrigen) {
        this.idProformaOrigen = idProformaOrigen;
    }

    public String getDocumentoOrigen() {
        return documentoOrigen;
    }

    public void setDocumentoOrigen(String documentoOrigen) {
        this.documentoOrigen = documentoOrigen;
    }

    public String getLocalizador() {
        return localizador;
    }

    public void setLocalizador(String localizador) {
        this.localizador = localizador;
    }

    public String getAlmacen() {
        return almacen;
    }

    public void setAlmacen(String almacen) {
        this.almacen = almacen;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getEstadoActual() {
        return estadoActual;
    }

    public void setEstadoActual(String estadoActual) {
        this.estadoActual = estadoActual;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(String fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }
}
