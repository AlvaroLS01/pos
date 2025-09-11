package com.comerzzia.pos.gui.mantenimientos.fidelizados;

import javafx.beans.property.SimpleStringProperty;

public class SexoGui {

	private SimpleStringProperty codigo;
	
	private SimpleStringProperty valor;
	
	public SexoGui(){
		
	}
	
	public SexoGui(String codigo, String valor){
		this.codigo = new SimpleStringProperty(codigo);
		this.valor = new SimpleStringProperty(valor);
	}

	public SimpleStringProperty codigoProperty() {
		return codigo;
	}
	
	public String getCodigo(){
		return codigo.get();
	}

	public void setCodigo(SimpleStringProperty codigo) {
		this.codigo = codigo;
	}

	public SimpleStringProperty valorProperty() {
		return valor;
	}
	
	public String getValor(){
		return valor.get();
	}

	public void setValor(SimpleStringProperty valor) {
		this.valor = valor;
	}
	
	public String toString(){
		return valor.getValue();
	}
}
