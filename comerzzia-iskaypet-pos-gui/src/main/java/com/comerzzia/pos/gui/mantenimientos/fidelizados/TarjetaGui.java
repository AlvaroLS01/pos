package com.comerzzia.pos.gui.mantenimientos.fidelizados;

import com.comerzzia.api.model.loyalty.TarjetaBean;

import javafx.beans.property.SimpleStringProperty;

public class TarjetaGui {
	
	private TarjetaBean tarjeta;
	private SimpleStringProperty numeroTarjeta;
	
	public TarjetaGui(TarjetaBean tarjeta){
		this.tarjeta = tarjeta;
		this.numeroTarjeta = new SimpleStringProperty(tarjeta.getNumeroTarjeta());
	}

	public TarjetaBean getTarjeta(){
		return tarjeta;
	}
	
	public SimpleStringProperty numeroTarjetaProperty(){
		return numeroTarjeta;
	}
	
	public String getNumeroTarjeta(){
		return numeroTarjeta.get();
	}
	
}
