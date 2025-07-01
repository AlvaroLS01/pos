
package com.comerzzia.pos.gui.sales.layaway.items;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.validation.ValidationFormGui;
import com.comerzzia.pos.core.gui.validation.validators.date.IsValidDate;

@Component
@Scope("prototype")
public class FormularioDetalleApartadoBean extends ValidationFormGui{
	
	@IsValidDate
	private String fechaRecogida;
	
	public FormularioDetalleApartadoBean() {
	
	}

	public String getFecha() {
		return fechaRecogida;
	}

	public void setFechaRecogida(String fecha) {
		this.fechaRecogida = fecha;
	}

	@Override
	public void clearForm() {
		fechaRecogida = "";
	}

}
