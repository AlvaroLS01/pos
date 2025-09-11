package com.comerzzia.pos.gui.mantenimientos.fidelizados;

import com.comerzzia.api.model.loyalty.EstadoCivilBean;

import javafx.beans.property.SimpleStringProperty;

public class EstadoCivilGui {

	private SimpleStringProperty codEstadoCivil;
	
	private SimpleStringProperty desEstadoCivil;
	
	public EstadoCivilGui(EstadoCivilBean estadoCivil){
		this.codEstadoCivil = new SimpleStringProperty(estadoCivil.getCodestcivil() != null? estadoCivil.getCodestcivil() : "");
		this.desEstadoCivil = new SimpleStringProperty(estadoCivil.getDesestcivil() != null? estadoCivil.getDesestcivil() : "");
	}

	public SimpleStringProperty codEstadoCivilProperty() {
		return codEstadoCivil;
	}

	public void setCodEstadoCivil(SimpleStringProperty codEstadoCivil) {
		this.codEstadoCivil = codEstadoCivil;
	}
	
	public void setCodEstadoCivil(String codEstadoCivil) {
		this.codEstadoCivil = new SimpleStringProperty(codEstadoCivil);
	}

	public SimpleStringProperty desEstadoCivilProperty() {
		return desEstadoCivil;
	}

	public void setDesEstadoCivil(SimpleStringProperty desEstadoCivil) {
		this.desEstadoCivil = desEstadoCivil;
	}
	
	public void setDesEstadoCivil(String desEstadoCivil) {
		this.desEstadoCivil = new SimpleStringProperty(desEstadoCivil);
	}
	
	public String getCodEstadoCivil(){
		return codEstadoCivil.get();
	}
	
	public String getDesEstadoCivil(){
		return desEstadoCivil.get();
	}
	
	public String toString(){
		return desEstadoCivil.getValue();
	}
	
}
