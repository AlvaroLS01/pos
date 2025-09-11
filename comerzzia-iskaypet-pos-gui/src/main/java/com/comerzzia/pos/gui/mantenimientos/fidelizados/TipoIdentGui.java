package com.comerzzia.pos.gui.mantenimientos.fidelizados;

import javafx.beans.property.SimpleStringProperty;

import com.comerzzia.pos.persistence.tiposIdent.TiposIdentBean;

public class TipoIdentGui {

	private SimpleStringProperty codTipoIden;
	
	private SimpleStringProperty desTipoIden;
	
	private String claseValidacion;
	
	public TipoIdentGui(){
		
	}
	
	public TipoIdentGui(String codTipoIden, String desTipoIden){
		this.codTipoIden = new SimpleStringProperty(codTipoIden);
		this.desTipoIden = new SimpleStringProperty(desTipoIden);
	}

	public TipoIdentGui(TiposIdentBean identidad) {
		this.codTipoIden = new SimpleStringProperty(identidad.getCodTipoIden());
		this.claseValidacion = identidad.getClaseValidacion();
		this.desTipoIden = new SimpleStringProperty(identidad.getDesTipoIden());
	}

	public SimpleStringProperty codigoProperty() {
		return codTipoIden;
	}
	
	public String getCodigo(){
		return codTipoIden.get();
	}

	public void setCodigo(SimpleStringProperty codigo) {
		this.codTipoIden = codigo;
	}

	public SimpleStringProperty valorProperty() {
		return desTipoIden;
	}
	
	public String getValor(){
		return desTipoIden.get();
	}

	public void setValor(SimpleStringProperty valor) {
		this.desTipoIden = valor;
	}
	
	public String getClaseValidacion() {
		return claseValidacion;
	}

	public void setClaseValidacion(String claseValidacion) {
		this.claseValidacion = claseValidacion;
	}

	public String toString(){
		return desTipoIden.getValue();
	}
}
