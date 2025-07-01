
package com.comerzzia.pos.gui.sales.layaway.items;

import java.math.BigDecimal;

import com.comerzzia.omnichannel.facade.model.deprecated.apartados.ApartadosCabeceraBean;
import com.comerzzia.omnichannel.facade.model.deprecated.apartados.detalle.ApartadosDetalleBean;
import com.comerzzia.omnichannel.facade.service.basket.retail.RetailBasketItem;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;


public class LineaArticuloApartadoRow {
	
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
    private RetailBasketItem linea;
    
    public LineaArticuloApartadoRow(){
    	
    	articulo = new SimpleStringProperty();
    	descripcion = new SimpleStringProperty();
    	desglose1 = new SimpleStringProperty();
    	desglose2 = new SimpleStringProperty();
    	pvp = new SimpleObjectProperty<BigDecimal>();
    	cantidad = new SimpleObjectProperty<BigDecimal>();
    	descuento = new SimpleObjectProperty<BigDecimal>();
    	importeTotal = new SimpleObjectProperty<BigDecimal>();
    }
    
    public LineaArticuloApartadoRow(ApartadosDetalleBean detalle){
    	
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
