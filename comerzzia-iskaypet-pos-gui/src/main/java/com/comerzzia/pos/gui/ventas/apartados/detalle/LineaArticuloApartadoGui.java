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
package com.comerzzia.pos.gui.ventas.apartados.detalle;

import java.math.BigDecimal;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import com.comerzzia.pos.persistence.apartados.ApartadosCabeceraBean;
import com.comerzzia.pos.persistence.apartados.detalle.ApartadosDetalleBean;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;


public class LineaArticuloApartadoGui {
	
	private SimpleStringProperty articulo;
    private SimpleStringProperty descripcion;
    private SimpleStringProperty desglose1;
    private SimpleStringProperty desglose2;
    private SimpleObjectProperty<BigDecimal> pvp;
    private SimpleObjectProperty<BigDecimal> cantidad;
    private SimpleObjectProperty<BigDecimal> descuento;
    private SimpleObjectProperty<BigDecimal> importeTotal;
    private boolean vendido;
    private ApartadosDetalleBean detalle;
    private LineaTicket linea;
    
    public LineaArticuloApartadoGui(){
    	
    	articulo = new SimpleStringProperty();
    	descripcion = new SimpleStringProperty();
    	desglose1 = new SimpleStringProperty();
    	desglose2 = new SimpleStringProperty();
    	pvp = new SimpleObjectProperty<BigDecimal>();
    	cantidad = new SimpleObjectProperty<BigDecimal>();
    	descuento = new SimpleObjectProperty<BigDecimal>();
    	importeTotal = new SimpleObjectProperty<BigDecimal>();
    }
    
    public LineaArticuloApartadoGui(ApartadosDetalleBean detalle){
    	
    	this();
    	    	
    	articulo.setValue(detalle.getCodart());  	
       	descripcion.setValue(detalle.getDesart());
    	desglose1.setValue(detalle.getDesglose1());
    	desglose2.setValue(detalle.getDesglose2());
    	pvp.setValue(detalle.getPrecio());
    	cantidad.setValue(detalle.getCantidad());
    	descuento.setValue(detalle.getDescuento());
    	importeTotal.setValue(detalle.getImporteTotal());
    	
    	this.detalle = detalle;
    	
    	vendido = detalle.getEstadoLineaApartado()==ApartadosCabeceraBean.ESTADO_FINALIZADO;
    }
    
    public boolean isVendido(){
    	return vendido;
    }
    
	public SimpleStringProperty getArticuloProperty() {
		return articulo;
	}
	
	public SimpleStringProperty getDescripcionProperty() {
		return descripcion;
	}
	
	public SimpleStringProperty getDesglose1Property() {
		return desglose1;
	}
	
	public SimpleStringProperty getDesglose2Property() {
		return desglose2;
	}
	
	public SimpleObjectProperty<BigDecimal> getPvpProperty() {
		return pvp;
	}
	
	public SimpleObjectProperty<BigDecimal> getCantidadProperty() {
		return cantidad;
	}
	
	public SimpleObjectProperty<BigDecimal> getDescuentoProperty() {
		return descuento;
	}
	
	public SimpleObjectProperty<BigDecimal> getImporteTotalProperty() {
		return importeTotal;
	}       

	public ApartadosDetalleBean getDetalle(){
		return detalle;
	}
	
}
