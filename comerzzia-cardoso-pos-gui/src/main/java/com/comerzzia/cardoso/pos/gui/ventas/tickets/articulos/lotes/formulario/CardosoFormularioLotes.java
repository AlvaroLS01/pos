package com.comerzzia.cardoso.pos.gui.ventas.tickets.articulos.lotes.formulario;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.comerzzia.pos.core.gui.validation.FormularioGui;
import com.comerzzia.pos.core.gui.validation.validators.number.esnumerico.EsNumerico;

/**
 * GAP - PERSONALIZACIONES V3 - LOTES
 */
public class CardosoFormularioLotes extends FormularioGui{

	@NotNull(message = "Debe rellenar el campo de lote")
	@NotEmpty(message = "Debe rellenar el campo de lote")
	private String lote;

	@NotNull(message = "Debe rellenar el campo de cantidad")
	@NotEmpty(message = "Debe rellenar el campo de cantidad")
	@EsNumerico(decimales = 3)
	private String cantidad;

	@Override
	public void limpiarFormulario(){
		lote = "";
		cantidad = "";
	}

	public String getLote(){
		return lote;
	}

	public void setLote(String lote){
		this.lote = lote;
	}

	public String getCantidad(){
		return cantidad;
	}

	public void setCantidad(String cantidad){
		this.cantidad = cantidad;
	}

}
