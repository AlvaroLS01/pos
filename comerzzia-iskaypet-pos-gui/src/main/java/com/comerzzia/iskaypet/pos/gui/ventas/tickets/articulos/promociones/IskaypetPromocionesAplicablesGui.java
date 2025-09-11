package com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.promociones;

import javafx.beans.property.SimpleStringProperty;

public class IskaypetPromocionesAplicablesGui {

	private SimpleStringProperty idPromocion;
	private SimpleStringProperty desPromocion;
	private SimpleStringProperty idTipoPromocion;

	public IskaypetPromocionesAplicablesGui(){
	}

	public IskaypetPromocionesAplicablesGui(SimpleStringProperty idPromocion, SimpleStringProperty desPromocion, SimpleStringProperty idTipoPromocion){
		this.idPromocion = idPromocion;
		this.desPromocion = desPromocion;
		this.setIdTipoPromocion(idTipoPromocion);
	}

	public String getIdPromocion(){
		return idPromocion.getValue();
	}

	public void setIdPromocion(SimpleStringProperty codPromocion){
		this.idPromocion = codPromocion;
	}

	public String getDesPromocion(){
		return desPromocion.getValue();
	}

	public void setDesPromocion(SimpleStringProperty desPromocion){
		this.desPromocion = desPromocion;
	}

	public SimpleStringProperty getIdTipoPromocion() {
		return idTipoPromocion;
	}

	public void setIdTipoPromocion(SimpleStringProperty idTipoPromocion) {
		this.idTipoPromocion = idTipoPromocion;
	}

}
