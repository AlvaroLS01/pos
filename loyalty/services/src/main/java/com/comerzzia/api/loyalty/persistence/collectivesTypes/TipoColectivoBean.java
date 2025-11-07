package com.comerzzia.api.loyalty.persistence.collectivesTypes;

public class TipoColectivoBean extends TipoColectivoKey {

	private static final long serialVersionUID = -6703497863931027613L;

	private String destipcolectivo;

    private String ambitoAplicacion;
    
    private String privado;
    
    //INICIO ATRIBUTOS PERSONALIZADOS--------------------------------------------
    
    //FIN ATRIBUTOS PERSONALIZADOS-----------------------------------------------


    public String getDestipcolectivo() {
        return destipcolectivo;
    }

    public void setDestipcolectivo(String destipcolectivo) {
        this.destipcolectivo = destipcolectivo == null ? null : destipcolectivo.trim();
    }

    public String getAmbitoAplicacion() {
        return ambitoAplicacion;
    }

    public void setAmbitoAplicacion(String ambitoAplicacion) {
        this.ambitoAplicacion = ambitoAplicacion == null ? null : ambitoAplicacion.trim();
    }
    
    public String getPrivado() {
        return privado;
    }

    public void setPrivado(String privado) {
        this.privado = privado == null ? null : privado.trim();
    }
    
    //INICIO MÉTODOS PERSONALIZADOS--------------------------------------------
    
    //FIN MÉTODOS PERSONALIZADOS-----------------------------------------------

}