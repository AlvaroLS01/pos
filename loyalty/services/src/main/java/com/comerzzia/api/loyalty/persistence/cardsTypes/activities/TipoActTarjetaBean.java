package com.comerzzia.api.loyalty.persistence.cardsTypes.activities;

public class TipoActTarjetaBean extends TipoActTarjetaKey {

	private static final long serialVersionUID = -8618202383687614205L;

	private String codmedpag;

    private String desmedpag;
    
    //INICIO ATRIBUTOS PERSONALIZADOS--------------------------------------------
    
    //FIN ATRIBUTOS PERSONALIZADOS-----------------------------------------------


    public String getCodmedpag() {
        return codmedpag;
    }

    public void setCodmedpag(String codmedpag) {
        this.codmedpag = codmedpag == null ? null : codmedpag.trim();
    }

    public String getDesmedpag() {
        return desmedpag;
    }

    public void setDesmedpag(String desmedpag) {
        this.desmedpag = desmedpag == null ? null : desmedpag.trim();
    }
    
    //INICIO MÉTODOS PERSONALIZADOS--------------------------------------------
    
    //FIN MÉTODOS PERSONALIZADOS-----------------------------------------------

}