package com.comerzzia.pos.gui.mantenimientos.fidelizados;

import java.math.BigDecimal;
import java.util.Date;

import com.comerzzia.api.model.sales.ArticuloAlbaranVentaBean;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class VentaGui {
	
	private SimpleObjectProperty<Date> fecha;
	private SimpleStringProperty articulo;
	private SimpleStringProperty descripcion;
	private SimpleStringProperty desglose1;
	private SimpleStringProperty desglose2;
	private SimpleObjectProperty<BigDecimal> cantidad;
	private SimpleObjectProperty<BigDecimal> importe;
	private SimpleStringProperty codTienda;
	
	public VentaGui(ArticuloAlbaranVentaBean articulo){
		this.fecha = new SimpleObjectProperty<Date>(articulo.getFecha());
		this.articulo = new SimpleStringProperty(articulo.getCodArticulo()== null? "":articulo.getCodArticulo());
		this.descripcion = new SimpleStringProperty(articulo.getDesArticulo()== null? "":articulo.getDesArticulo());
		this.desglose1 = new SimpleStringProperty(articulo.getDesglose1()== null? "":articulo.getDesglose1());
		this.desglose2 = new SimpleStringProperty(articulo.getDesglose2()== null? "":articulo.getDesglose2());
		BigDecimal cantidadBD = new BigDecimal(articulo.getCantidad());
		BigDecimal importeBD = new BigDecimal(articulo.getImporte());
		this.cantidad = new SimpleObjectProperty<BigDecimal>(cantidadBD);
		this.importe = new SimpleObjectProperty<BigDecimal>(importeBD);
		this.codTienda = new SimpleStringProperty(articulo.getCodalm()== null? "":articulo.getCodalm());
	}
	
	public SimpleObjectProperty<Date> fechaProperty() {
		return fecha;
	}
	
	public Date getFecha(){
		return fecha.get();
	}
	
	public SimpleStringProperty articuloProperty() {
		return articulo;
	}
	
	public String getArticulo(){
		return articulo.get();
	}
	
	public SimpleStringProperty descripcionProperty() {
		return descripcion;
	}
	
	public String getDescripcion(){
		return descripcion.get();
	}
	
	public SimpleStringProperty desglose1Property() {
		return desglose1;
	}
	
	public String getDesglose1(){
		return desglose1.get();
	}
	
	public SimpleStringProperty desglose2Property() {
		return desglose2;
	}
	
	public String getDesglose2(){
		return desglose2.get();
	}
	
	public SimpleObjectProperty<BigDecimal> cantidadProperty() {
		return cantidad;
	}
	
	public BigDecimal getCantidad(){
		return cantidad.get();
	}
	
	public SimpleObjectProperty<BigDecimal> importeProperty() {
		return importe;
	}
	
	public BigDecimal getImporte(){
		return importe.get();
	}
	
	public SimpleStringProperty codTiendaProperty() {
		return codTienda;
	}
	
	public String getCodTienda(){
		return codTienda.get();
	}

}
