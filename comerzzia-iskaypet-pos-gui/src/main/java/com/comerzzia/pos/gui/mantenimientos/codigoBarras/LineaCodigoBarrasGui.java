/**
 * ComerZZia 3.0
 *
 * Copyright (c) 2008-2015 Comerzzia, S.L.  All Rights Reserved.
 *
 * THIS WORK IS  SUBJECT  TO  SPAIN  AND  INTERNATIONAL  COPYRIGHT  LAWS  AND
 * TREATIES.   NO  PART  OF  THIS  WORK MAY BE  USED,  PRACTICED,  PERFORMED
 * COPIED, DISTRIBUTED, REVISED, MODIFIED, TRANSLATED,  ABRIDGED, CONDENSED,
 * EXPANDED,  COLLECTED,  COMPILED,  LINKED,  RECAST, TRANSFORMED OR ADAPTED
 * WITHOUT THE PRIOR WRITTEN CONSENT OF COMERZZIA, S.L. ANY USE OR EXPLOITATION
 * OF THIS WORK WITHOUT AUTHORIZATION COULD SUBJECT THE PERPETRATOR TO
 * CRIMINAL AND CIVIL LIABILITY.
 *
 * CONSULT THE END USER LICENSE AGREEMENT FOR INFORMATION ON ADDITIONAL
 * RESTRICTIONS.
 */
package com.comerzzia.pos.gui.mantenimientos.codigoBarras;

import javafx.beans.property.SimpleStringProperty;

import com.comerzzia.pos.persistence.codBarras.CodigoBarrasBean;


public class LineaCodigoBarrasGui {

	SimpleStringProperty descripcion;
	SimpleStringProperty fidelizacion;
	SimpleStringProperty prefijo;
	SimpleStringProperty cantidad;
	SimpleStringProperty precio;
	SimpleStringProperty ticket;
	SimpleStringProperty articulo;
	
	CodigoBarrasBean codBarras;
	
	public LineaCodigoBarrasGui(CodigoBarrasBean codBarras){
		
		descripcion = new SimpleStringProperty();
		fidelizacion = new SimpleStringProperty();
		prefijo = new SimpleStringProperty();
		cantidad = new SimpleStringProperty();
		precio = new SimpleStringProperty();
		ticket = new SimpleStringProperty();
		articulo = new SimpleStringProperty();
		
		descripcion.setValue(codBarras.getDescripcion());
		fidelizacion.setValue(codBarras.getFidelizacion());
		prefijo.setValue(codBarras.getPrefijo());
		cantidad.setValue(codBarras.getCantidad());
		precio.setValue(codBarras.getPrecio());
		ticket.setValue(codBarras.getCodticket());
		articulo.setValue(codBarras.getCodart());
	
		this.codBarras = codBarras;
	}

	public SimpleStringProperty getDescripcionProperty() {
		return descripcion;
	}

	public void setDescripcionProperty(SimpleStringProperty descripcion) {
		this.descripcion = descripcion;
	}

	public SimpleStringProperty getFidelizacionProperty() {
		return fidelizacion;
	}

	public void setFidelizacionProperty(SimpleStringProperty fidelizacion) {
		this.fidelizacion = fidelizacion;
	}

	public SimpleStringProperty getPrefijoProperty() {
		return prefijo;
	}

	public void setPrefijoProperty(SimpleStringProperty prefijo) {
		this.prefijo = prefijo;
	}

	public SimpleStringProperty getCantidadProperty() {
		return cantidad;
	}

	public void setCantidadProperty(SimpleStringProperty cantidad) {
		this.cantidad = cantidad;
	}

	public SimpleStringProperty getPrecioProperty() {
		return precio;
	}

	public void setPrecioProperty(SimpleStringProperty precio) {
		this.precio = precio;
	}

	public SimpleStringProperty getTicketProperty() {
		return ticket;
	}

	public void setTicketProperty(SimpleStringProperty ticket) {
		this.ticket = ticket;
	}

	public SimpleStringProperty getArticuloProperty() {
		return articulo;
	}

	public void setArticuloProperty(SimpleStringProperty articulo) {
		this.articulo = articulo;
	}	
	
	public CodigoBarrasBean getCodBarras(){
		return codBarras;
	}
}
