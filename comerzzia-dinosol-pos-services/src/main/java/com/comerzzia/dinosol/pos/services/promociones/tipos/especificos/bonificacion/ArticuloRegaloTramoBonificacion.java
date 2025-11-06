package com.comerzzia.dinosol.pos.services.promociones.tipos.especificos.bonificacion;

import java.math.BigDecimal;

import com.comerzzia.core.util.xml.XMLDocumentNode;
import com.comerzzia.core.util.xml.XMLDocumentNodeNotFoundException;

public class ArticuloRegaloTramoBonificacion {

	private String codArticulo;
	private BigDecimal cantidad;
	private BigDecimal precio;
	
	public ArticuloRegaloTramoBonificacion(XMLDocumentNode nodo) throws XMLDocumentNodeNotFoundException {
		codArticulo = nodo.getNodo("codarticulo").getValue();
		cantidad = nodo.getNodo("cantidad").getValueAsBigDecimal();
		precio = nodo.getNodo("precio").getValueAsBigDecimal();
		
	}

	public String getCodArticulo() {
		return codArticulo;
	}

	public void setCodArticulo(String codArticulo) {
		this.codArticulo = codArticulo;
	}

	public BigDecimal getCantidad() {
		return cantidad;
	}

	public void setCantidad(BigDecimal cantidad) {
		this.cantidad = cantidad;
	}
	
	public BigDecimal getPrecio() {
		return precio;
	}

	public void setPrecio(BigDecimal precio) {
		this.precio = precio;
	}

}
