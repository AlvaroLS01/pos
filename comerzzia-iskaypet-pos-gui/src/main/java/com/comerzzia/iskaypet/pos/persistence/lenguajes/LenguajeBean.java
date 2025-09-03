package com.comerzzia.iskaypet.pos.persistence.lenguajes;

public class LenguajeBean extends LenguajeKey {
    private String deslengua;

    private String activo;
    
    //INICIO ATRIBUTOS PERSONALIZADOS--------------------------------------------
    
    //FIN ATRIBUTOS PERSONALIZADOS-----------------------------------------------


    public String getDeslengua() {
        return deslengua;
    }

    public void setDeslengua(String deslengua) {
        this.deslengua = deslengua == null ? null : deslengua.trim();
    }

    public String getActivo() {
        return activo;
    }

    public void setActivo(String activo) {
        this.activo = activo == null ? null : activo.trim();
    }
    
    //INICIO MÉTODOS PERSONALIZADOS--------------------------------------------
    
    //FIN MÉTODOS PERSONALIZADOS-----------------------------------------------

}