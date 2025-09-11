package com.comerzzia.pos.gui.mantenimientos.fidelizados.tiendas;

import org.hibernate.validator.constraints.NotBlank;

import com.comerzzia.api.model.core.TiendaBean;

import javafx.beans.property.SimpleStringProperty;

public class TiendaGui {

	@NotBlank
	private SimpleStringProperty codTienda;
	
	@NotBlank
	private SimpleStringProperty desTienda;
	
	public TiendaGui(TiendaBean tienda){
		codTienda = new SimpleStringProperty(tienda.getCodAlm());
		desTienda = new SimpleStringProperty(tienda.getDesAlm());
	}

	public SimpleStringProperty codTiendaProperty() {
		return codTienda;
	}

	public String getCodTienda() {
		return codTienda.getValue();
	}

	public SimpleStringProperty desTiendaProperty() {
		return desTienda;
	}
	
	public String getDesTienda() {
		return desTienda.getValue();
	}
	
	public String toString(){
		return desTienda.getValue();
	}
	
}
