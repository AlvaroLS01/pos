package com.comerzzia.api.loyalty.persistence.civilStates;

public class EstadoCivilBean extends EstadoCivilKey {

	private static final long serialVersionUID = -6176374523984958113L;

	private String desestcivil;

    //INICIO ATRIBUTOS PERSONALIZADOS--------------------------------------------
    
    //FIN ATRIBUTOS PERSONALIZADOS-----------------------------------------------


    public String getDesestcivil() {
        return desestcivil;
    }

    public void setDesestcivil(String desestcivil) {
        this.desestcivil = desestcivil == null ? null : desestcivil.trim();
    }

    //INICIO MÉTODOS PERSONALIZADOS--------------------------------------------
    
    //FIN MÉTODOS PERSONALIZADOS-----------------------------------------------

}