
package com.comerzzia.pos.gui.sales.layaway.items.marcarVenta;

import java.math.BigDecimal;

import com.comerzzia.omnichannel.facade.model.deprecated.apartados.detalle.ApartadosDetalleBean;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;


public class LineaVentaApartadoRow {
	
	SimpleBooleanProperty lineaSelec;
	SimpleStringProperty codArt;
	SimpleStringProperty desArt;
	SimpleObjectProperty<BigDecimal> cantidad;
	SimpleObjectProperty<BigDecimal> importe;
	SimpleObjectProperty<BigDecimal> dto;
	SimpleStringProperty desglose1;
	SimpleStringProperty desglose2;
	SimpleObjectProperty<BigDecimal> precio;
	
	ApartadosDetalleBean articulo;

	public LineaVentaApartadoRow(ApartadosDetalleBean articulo){
		
		this.lineaSelec = new SimpleBooleanProperty();
		codArt = new SimpleStringProperty();
		desArt = new SimpleStringProperty();
		cantidad = new SimpleObjectProperty<BigDecimal>();
		importe = new SimpleObjectProperty<BigDecimal>();
		dto = new SimpleObjectProperty<BigDecimal>();
		precio = new SimpleObjectProperty<BigDecimal>();
		desglose1 = new SimpleStringProperty();
		desglose2 = new SimpleStringProperty();
		
		codArt.setValue(articulo.getCodart());
		desArt.setValue(articulo.getDesart());
		cantidad.setValue(articulo.getCantidad());
		importe.setValue(articulo.getImporteTotal());
		dto.setValue(articulo.getDescuento());
		precio.setValue(articulo.getPrecio());
		desglose1.setValue(articulo.getDesglose1());
		desglose2.setValue(articulo.getDesglose2());		
		lineaSelec.setValue(articulo.isLineaSeleccionadaVenta());
		
		this.articulo = articulo;
	}
	
	public ApartadosDetalleBean getArticulo(){
		return articulo;
	}

	public SimpleBooleanProperty lineaSelecProperty() {
		return lineaSelec;
	}

	public void setLineaSelec(boolean lineaSelec) {
		this.lineaSelec.setValue(lineaSelec);
	}
	
	public boolean isLineaSelec(){
		return lineaSelec.getValue();
	}

	public SimpleStringProperty getCodArtProperty() {
		return codArt;
	}

	public void setCodArt(SimpleStringProperty codArt) {
		this.codArt = codArt;
	}

	public SimpleStringProperty getDesArtProperty() {
		return desArt;
	}

	public void setDesArt(SimpleStringProperty desArt) {
		this.desArt = desArt;
	}

	public SimpleObjectProperty<BigDecimal> getCantidadProperty() {
		return cantidad;
	}

	public void setCantidad(SimpleObjectProperty<BigDecimal> cantidad) {
		this.cantidad = cantidad;
	}

	public SimpleObjectProperty<BigDecimal> getImporteProperty() {
		return importe;
	}

	public void setImporte(SimpleObjectProperty<BigDecimal> importe) {
		this.importe = importe;
	}

	public SimpleObjectProperty<BigDecimal> getDtoProperty() {
		return dto;
	}

	public void setDto(SimpleObjectProperty<BigDecimal> dto) {
		this.dto = dto;
	}

	public SimpleStringProperty getDesglose1Property() {
		return desglose1;
	}

	public void setDesglose1(SimpleStringProperty desglose1) {
		this.desglose1 = desglose1;
	}

	public SimpleStringProperty getDesglose2Property() {
		return desglose2;
	}

	public void setDesglose2(SimpleStringProperty desglose2) {
		this.desglose2 = desglose2;
	}

	public SimpleObjectProperty<BigDecimal> getPrecioProperty() {
		return precio;
	}

	public void setPrecio(SimpleObjectProperty<BigDecimal> precio) {
		this.precio = precio;
	}
	
}
