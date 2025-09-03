package com.comerzzia.iskaypet.pos.gui.mantenimientos.fidelizados.datosgenerales.lenguaje;

import com.comerzzia.iskaypet.pos.persistence.lenguajes.LenguajeBean;

public class LenguajeGui {
    
    private String codLenguaje;
    
    private String descLenguaje;
    
    private LenguajeBean lenguaje;
    
    public LenguajeGui(){
    }
    
    public LenguajeGui(LenguajeBean lenguaje){
        
        this.codLenguaje = lenguaje.getCodlengua();
        this.descLenguaje = lenguaje.getDeslengua();
        this.lenguaje = lenguaje;
    }

	public String getCodLenguaje() {
		return codLenguaje;
	}

	public void setCodLenguaje(String codLenguaje) {
		this.codLenguaje = codLenguaje;
	}

	public String getDescLenguaje() {
		return descLenguaje;
	}

	public void setDescLenguaje(String descLenguaje) {
		this.descLenguaje = descLenguaje;
	}

	public LenguajeBean getLenguaje() {
		return lenguaje;
	}

	public void setLenguaje(LenguajeBean lenguaje) {
		this.lenguaje = lenguaje;
	}

}
