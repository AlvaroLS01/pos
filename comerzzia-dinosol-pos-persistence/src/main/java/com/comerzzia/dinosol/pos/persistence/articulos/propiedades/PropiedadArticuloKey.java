package com.comerzzia.dinosol.pos.persistence.articulos.propiedades;

public class PropiedadArticuloKey {
    private String uidActividad;

    private String idClase;

    private String idObjeto;

    private String parametro;

    public String getUidActividad() {
        return uidActividad;
    }

    public void setUidActividad(String uidActividad) {
        this.uidActividad = uidActividad == null ? null : uidActividad.trim();
    }

    public String getIdClase() {
        return idClase;
    }

    public void setIdClase(String idClase) {
        this.idClase = idClase == null ? null : idClase.trim();
    }

    public String getIdObjeto() {
        return idObjeto;
    }

    public void setIdObjeto(String idObjeto) {
        this.idObjeto = idObjeto == null ? null : idObjeto.trim();
    }

    public String getParametro() {
        return parametro;
    }

    public void setParametro(String parametro) {
        this.parametro = parametro == null ? null : parametro.trim();
    }
}