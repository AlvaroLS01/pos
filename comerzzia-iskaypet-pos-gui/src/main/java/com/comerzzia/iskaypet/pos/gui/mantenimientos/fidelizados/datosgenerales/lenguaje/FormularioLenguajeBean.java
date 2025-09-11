package com.comerzzia.iskaypet.pos.gui.mantenimientos.fidelizados.datosgenerales.lenguaje;

import javax.validation.constraints.Size;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.validation.FormularioGui;

@Component
@Scope("prototype")
public class FormularioLenguajeBean extends FormularioGui{
    
    @Size (max = 20)
    private String descLenguaje;
    
    @Size (max = 6)
    private String codLenguaje;
    
    public FormularioLenguajeBean(){
    }

    public FormularioLenguajeBean(String codLenguaje, String descLenguaje){
        
        this.descLenguaje = descLenguaje;
        this.codLenguaje = codLenguaje;
    }
    
    public String getDescLenguaje() {
		return descLenguaje;
	}

	public void setDescLenguaje(String descLenguaje) {
		this.descLenguaje = descLenguaje;
	}

	public String getCodLenguaje() {
		return codLenguaje;
	}

	public void setCodLenguaje(String codLenguaje) {
		this.codLenguaje = codLenguaje;
	}

	@Override
    public void limpiarFormulario() {
		descLenguaje = "";
		codLenguaje = "";
    }
    
}
