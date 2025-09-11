package com.comerzzia.pos.gui.mantenimientos.fidelizados.colectivos;

import org.hibernate.validator.constraints.NotBlank;

import com.comerzzia.api.model.loyalty.ColectivoBean;

import javafx.beans.property.SimpleStringProperty;

public class ColectivoAyudaGui {

	@NotBlank
	private SimpleStringProperty codColectivo;
	
	@NotBlank
	private SimpleStringProperty desColectivo;
	
	public ColectivoAyudaGui(ColectivoBean colectivo){
		codColectivo = new SimpleStringProperty(colectivo.getCodColectivo());
		desColectivo = new SimpleStringProperty(colectivo.getDesColectivo());
	}

	public SimpleStringProperty codColectivoProperty() {
		return codColectivo;
	}

	public String getCodColectivo() {
		return codColectivo.getValue();
	}

	public SimpleStringProperty desColectivoProperty() {
		return desColectivo;
	}
	
	public String getDesColectivo() {
		return desColectivo.getValue();
	}
	
	public String toString(){
		return desColectivo.getValue();
	}
	
}
