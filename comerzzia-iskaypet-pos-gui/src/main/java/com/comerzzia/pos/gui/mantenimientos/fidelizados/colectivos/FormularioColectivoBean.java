package com.comerzzia.pos.gui.mantenimientos.fidelizados.colectivos;

import javax.validation.constraints.Size;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.validation.FormularioGui;

@Component
@Scope("prototype")
public class FormularioColectivoBean extends FormularioGui{

	@Size (max = 255)
    private String descColectivo;
    
    @Size (max = 40)
    private String codColectivo;
    
    public FormularioColectivoBean(){
    	
    }

    public FormularioColectivoBean(String codColectivo, String descColectivo){
    	this.codColectivo = codColectivo;
    	this.descColectivo = descColectivo;
    }

	public String getDescColectivo() {
		return descColectivo;
	}

	public void setDescColectivo(String descColectivo) {
		this.descColectivo = descColectivo;
	}

	public String getCodColectivo() {
		return codColectivo;
	}

	public void setCodColectivo(String codColectivo) {
		this.codColectivo = codColectivo;
	}

	@Override
	public void limpiarFormulario() {
		descColectivo = "";
		codColectivo = "";
	}

}
