package com.comerzzia.dinosol.pos.persistence.tickets.sad;

public class TicketAnexoSadBean extends TicketAnexoSadKey {
    private String uidTicketSad;
    
    //INICIO ATRIBUTOS PERSONALIZADOS--------------------------------------------
    
    //FIN ATRIBUTOS PERSONALIZADOS-----------------------------------------------


    public String getUidTicketSad() {
        return uidTicketSad;
    }

    public void setUidTicketSad(String uidTicketSad) {
        this.uidTicketSad = uidTicketSad == null ? null : uidTicketSad.trim();
    }
    
    //INICIO MÉTODOS PERSONALIZADOS--------------------------------------------
    
    //FIN MÉTODOS PERSONALIZADOS-----------------------------------------------

}