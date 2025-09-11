package com.comerzzia.pos.gui.ventas.profesional.venta;

import java.math.BigDecimal;

import javafx.beans.property.SimpleObjectProperty;

import com.comerzzia.pos.gui.ventas.tickets.articulos.LineaTicketGui;
import com.comerzzia.pos.services.ticket.cupones.CuponAplicadoTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicketAbstract;
import com.comerzzia.pos.services.ticket.profesional.LineaTicketProfesional;

public class LineaTicketProfesionalGui extends LineaTicketGui {

	private SimpleObjectProperty<BigDecimal> precio;
	private SimpleObjectProperty<BigDecimal> importe;	
	private SimpleObjectProperty<BigDecimal> precioConIva;
	private SimpleObjectProperty<BigDecimal> importeConIva;

	public LineaTicketProfesionalGui(LineaTicketAbstract linea) {
		super(linea);
		precio = new SimpleObjectProperty<BigDecimal>(((LineaTicketProfesional) linea).getPrecioSinDto());
		importe = new SimpleObjectProperty<BigDecimal>(((LineaTicketProfesional) linea).getImporteConDto());
		precioConIva = new SimpleObjectProperty<BigDecimal>(((LineaTicketProfesional) linea).getPrecioTotalConDto());
		importeConIva = new SimpleObjectProperty<BigDecimal>(((LineaTicketProfesional) linea).getImporteTotalConDto());
	}

	public LineaTicketProfesionalGui(CuponAplicadoTicket cupon) {
		super(cupon);
	}

	public SimpleObjectProperty<BigDecimal> getPrecioProperty() {
		return precio;
	}

	public SimpleObjectProperty<BigDecimal> getPrecioConIvaProperty() {
		return precioConIva;
	}

	public void setPrecio(BigDecimal precio) {
		this.precio.set(precio);
	}

	public void setPrecioConIva(BigDecimal precioConIva) {
		this.precioConIva.set(precioConIva);
	}

	public SimpleObjectProperty<BigDecimal> getImporteConIvaProperty() {
		return importeConIva;
	}

	public void setImporteConIva(BigDecimal importeConIva) {
		this.importeConIva.set(importeConIva);
	}

	public SimpleObjectProperty<BigDecimal> getImporteProperty() {
		return importe;
	}

	public void setImporte(BigDecimal importe) {
		this.importe.set(importe);
	}

}
