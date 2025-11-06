package com.comerzzia.dinosol.pos.persistence.tickets;

public class TicketAnexoBean extends TicketAnexoKey {
    private Boolean tieneRecarga;

    private String operador;

    private String telefono;

    private String codValidacion;
    
    //INICIO ATRIBUTOS PERSONALIZADOS--------------------------------------------
    
    //FIN ATRIBUTOS PERSONALIZADOS-----------------------------------------------


    public Boolean getTieneRecarga() {
        return tieneRecarga;
    }

    public void setTieneRecarga(Boolean tieneRecarga) {
        this.tieneRecarga = tieneRecarga;
    }

    public String getOperador() {
        return operador;
    }

    public void setOperador(String operador) {
        this.operador = operador == null ? null : operador.trim();
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono == null ? null : telefono.trim();
    }

    public String getCodValidacion() {
        return codValidacion;
    }

    public void setCodValidacion(String codValidacion) {
        this.codValidacion = codValidacion == null ? null : codValidacion.trim();
    }
    
    //INICIO MÉTODOS PERSONALIZADOS--------------------------------------------
    
    //FIN MÉTODOS PERSONALIZADOS-----------------------------------------------

}