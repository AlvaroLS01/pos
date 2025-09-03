package com.comerzzia.iskaypet.pos.gui.ventas.tickets.pagos.mensajepromociones.gui;

import java.math.BigDecimal;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class MensajePromocionGui {
	
	private SimpleStringProperty descripcion;
	private SimpleObjectProperty<BigDecimal> descuento;
	
	public MensajePromocionGui(String descripcion, BigDecimal descuento){
		this.descripcion = new SimpleStringProperty(descripcion);
		this.descuento = new SimpleObjectProperty<BigDecimal>(descuento.setScale(3));
	}
	
	public String getDescripcion(){
		return descripcion.getValue();
	}

	public void setDescripcion(String descripcion){
		this.descripcion.setValue(descripcion);
	}

	public SimpleStringProperty getDescripcionProperty(){
		return descripcion;
	}
	
	public BigDecimal getDescuento(){
		return descuento.getValue();
	}

	public void setDescuento(BigDecimal descuento){
		this.descuento.setValue(descuento);
	}

	public SimpleObjectProperty<BigDecimal> getDescuentoProperty(){
		return descuento;
	}
}
