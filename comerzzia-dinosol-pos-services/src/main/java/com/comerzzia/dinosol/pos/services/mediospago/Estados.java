package com.comerzzia.dinosol.pos.services.mediospago;


public enum Estados {
	//Estados
	ESPERA("0"),
    PETICION("1"),
    CURSADA("2"),
    RESPUESTA("3"),
    TIMEOUT1("4"),
    RECOGIDA("5"),
    TIMEOUT2("6"),
    ERRORTERMINAL("7");

    // declaring private variable for getting values
    private final String codEstado;

	// getter method
    public String getCodEstado(){
        return this.codEstado;
    }

    // enum constructor - cannot be public or protected
    Estados(String codigoEstado){
        this.codEstado = codigoEstado;
    }

}
