package com.comerzzia.pos.gui.mantenimientos.fidelizados.tiendas;

import javax.validation.constraints.Size;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.validation.FormularioGui;

@Component
@Scope("prototype")
public class FormularioTiendaBean  extends FormularioGui{
	
	@Size (max = 20)
    private String descTienda;
    
    @Size (max = 4)
    private String codTienda;
    
    public FormularioTiendaBean(){
    	
    }

    public FormularioTiendaBean(String codTienda, String descTienda){
    	this.codTienda = codTienda;
    	this.descTienda = descTienda;
    }

	public String getDescTienda() {
		return descTienda;
	}

	public void setDescTienda(String descTienda) {
		this.descTienda = descTienda;
	}

	public String getCodTienda() {
		return codTienda;
	}

	public void setCodTienda(String codTienda) {
		this.codTienda = codTienda;
	}

	@Override
	public void limpiarFormulario() {
		descTienda = "";
		codTienda = "";
	}

}
