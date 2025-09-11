package com.comerzzia.pos.gui.mantenimientos.fidelizados;

import com.comerzzia.api.model.loyalty.ColectivosFidelizadoBean;

import javafx.beans.property.SimpleStringProperty;

public class ColectivoGui {

	private SimpleStringProperty codColectivo;
	
	private SimpleStringProperty desColectivo;
	
	private SimpleStringProperty tipoColectivo;
	
	public ColectivoGui(ColectivosFidelizadoBean colectivo){
		codColectivo = new SimpleStringProperty(colectivo.getCodColectivo() != null ? colectivo.getCodColectivo() : "");
		desColectivo = new SimpleStringProperty(colectivo.getDesColectivo() != null ? colectivo.getDesColectivo() : "");
		tipoColectivo = new SimpleStringProperty(colectivo.getDestipcolectivo() != null ? colectivo.getDestipcolectivo() : "");
	}

	public SimpleStringProperty propertyCodColectivo() {
		return codColectivo;
	}
	
	public String getCodColectivo(){
		return codColectivo.get();
	}

	public SimpleStringProperty propertyDesColectivo() {
		return desColectivo;
	}
	
	public String getDesColectivo(){
		return desColectivo.get();
	}

	public SimpleStringProperty propertyTipoColectivo(){
		return tipoColectivo;
	}

	public String getTipoColectivo(){
		return tipoColectivo.get();
	}
	
}
