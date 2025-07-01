
package com.comerzzia.pos.gui.sales.layaway;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.validation.ValidationFormGui;
import com.comerzzia.pos.core.gui.validation.validators.number.IsNumeric;

@Component
@Scope("prototype")
public class FormularioBusquedaApartadoBean extends ValidationFormGui{

	@IsNumeric (message = "El valor debe ser numérico.")
	String numApartado;
	
	public FormularioBusquedaApartadoBean() {
	}
	
	public void setNumApartado(String numApartado){
		this.numApartado = numApartado;
	}
	
	@Override
	public void clearForm() {
		numApartado = "";
	}

}
