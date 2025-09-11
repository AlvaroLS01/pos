package com.comerzzia.iskaypet.pos.api.rest.proformas.request;

import java.util.List;

public class ProformaRequestRest {

    String uidActividad;
    String apiKey;
    String idProforma;
    List<String> tiposDocumentos;
    String almacen;

    public ProformaRequestRest() {
    }

    public ProformaRequestRest(String uidActividad, String apiKey, String idProforma, List<String> tiposDocumentos, String almacen, String estado, String automatica, String fechaDesde, String fechaHasta) {
        this.uidActividad = uidActividad;
        this.apiKey = apiKey;
        this.idProforma = idProforma;
        this.tiposDocumentos = tiposDocumentos;
        this.almacen = almacen;
    }

    public String getIdProforma() {
        return idProforma;
    }

    public void setIdProforma(String idProforma) {
        this.idProforma = idProforma;
    }

    public String getUidActividad() {
        return uidActividad;
    }

    public void setUidActividad(String uidActividad) {
        this.uidActividad = uidActividad;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public List<String> getTiposDocumentos() {
        return tiposDocumentos;
    }

    public void setTiposDocumentos(List<String> tiposDocumentos) {
        this.tiposDocumentos = tiposDocumentos;
    }

    public String getAlmacen() {
        return almacen;
    }

    public void setAlmacen(String almacen) {
        this.almacen = almacen;
    }

}
