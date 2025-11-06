package com.comerzzia.dinosol.pos.persistence.enviosdomicilio;

import io.swagger.annotations.ApiModelProperty;

import com.comerzzia.dinosol.librerias.sad.client.model.TipoBulto;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class RutasTipoBultoBean extends TipoBulto{

	@JsonIgnore
	private int cantidad;
	
	@ApiModelProperty(value = "")
	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

}
