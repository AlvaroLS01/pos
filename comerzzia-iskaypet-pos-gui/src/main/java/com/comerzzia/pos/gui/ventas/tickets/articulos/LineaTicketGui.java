/**
 * ComerZZia 3.0
 * <p>
 * Copyright (c) 2008-2015 Comerzzia, S.L.  All Rights Reserved.
 * <p>
 * THIS WORK IS  SUBJECT  TO  SPAIN  AND  INTERNATIONAL  COPYRIGHT  LAWS  AND
 * TREATIES.   NO  PART  OF  THIS  WORK MAY BE  USED,  PRACTICED,  PERFORMED
 * COPIED, DISTRIBUTED, REVISED, MODIFIED, TRANSLATED,  ABRIDGED, CONDENSED,
 * EXPANDED,  COLLECTED,  COMPILED,  LINKED,  RECAST, TRANSFORMED OR ADAPTED
 * WITHOUT THE PRIOR WRITTEN CONSENT OF COMERZZIA, S.L. ANY USE OR EXPLOITATION
 * OF THIS WORK WITHOUT AUTHORIZATION COULD SUBJECT THE PERPETRATOR TO
 * CRIMINAL AND CIVIL LIABILITY.
 * <p>
 * CONSULT THE END USER LICENSE AGREEMENT FOR INFORMATION ON ADDITIONAL
 * RESTRICTIONS.
 */

package com.comerzzia.pos.gui.ventas.tickets.articulos;

import java.math.BigDecimal;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import com.comerzzia.pos.services.ticket.cupones.CuponAplicadoTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicketAbstract;
import com.comerzzia.pos.util.format.FormatUtil;


public class LineaTicketGui {
	
	protected SimpleStringProperty articulo;
	protected SimpleStringProperty descripcion;
	protected SimpleStringProperty desglose1;
	protected SimpleStringProperty desglose2;
	protected SimpleObjectProperty<BigDecimal> pvp;
	protected SimpleObjectProperty<BigDecimal> pvpConDto;
	protected SimpleObjectProperty<BigDecimal> cantidad;
	protected SimpleObjectProperty<BigDecimal> importe;
	protected SimpleObjectProperty<BigDecimal> importeTotalFinal;
	protected SimpleObjectProperty<BigDecimal> descuento;
	protected Integer idLinea;
	protected Boolean cupon;
	protected boolean lineaDocAjeno;
	protected SimpleStringProperty vendedor;
	protected boolean editable;

	public LineaTicketGui() {
		articulo = new SimpleStringProperty();
		descripcion = new SimpleStringProperty();
		desglose1 = new SimpleStringProperty("");
		desglose2 = new SimpleStringProperty("");
		pvp = new SimpleObjectProperty<BigDecimal>();
		pvpConDto = new SimpleObjectProperty<BigDecimal>();
		cantidad = new SimpleObjectProperty<BigDecimal>();
		importe = new SimpleObjectProperty<BigDecimal>();
		importeTotalFinal = new SimpleObjectProperty<BigDecimal>();
		descuento = new SimpleObjectProperty<BigDecimal>();
		vendedor = new SimpleStringProperty("");
		this.cupon = false;
		this.editable = true;
	}

	public LineaTicketGui(LineaTicketAbstract linea) {
		LineaTicket lineaTicket = (LineaTicket) linea;

		articulo = new SimpleStringProperty(linea.getArticulo().getCodArticulo());
		descripcion = new SimpleStringProperty(linea.getArticulo().getDesArticulo());
		desglose1 = new SimpleStringProperty(linea.getDesglose1());
		desglose2 = new SimpleStringProperty(linea.getDesglose2());
		pvp = new SimpleObjectProperty<BigDecimal>(linea.getPrecioTotalSinDto());
		pvpConDto = new SimpleObjectProperty<BigDecimal>(linea.getPrecioTotalConDto());
		cantidad = new SimpleObjectProperty<BigDecimal>(linea.getCantidad());
		importe = new SimpleObjectProperty<BigDecimal>(linea.getImporteTotalSinDto());
		descuento = new SimpleObjectProperty<BigDecimal>(linea.getDescuento());
		importeTotalFinal = new SimpleObjectProperty<BigDecimal>(linea.getImporteTotalConDto());
		idLinea = linea.getIdLinea();
		this.cupon = false;
		lineaDocAjeno = lineaTicket.getDocumentoOrigen() != null;
		if (linea.getVendedor() != null) {
			vendedor = new SimpleStringProperty(linea.getVendedor().getUsuario());
		} else {
			vendedor = new SimpleStringProperty("");
		}
		this.editable = linea.isEditable();
	}

	public LineaTicketGui(CuponAplicadoTicket cupon) {
		articulo = new SimpleStringProperty(cupon.getCodigo());
		descripcion = new SimpleStringProperty(cupon.getTextoPromocion());
		desglose1 = new SimpleStringProperty("");
		desglose2 = new SimpleStringProperty("");
		pvp = new SimpleObjectProperty<BigDecimal>();
		pvpConDto = new SimpleObjectProperty<BigDecimal>();
		cantidad = new SimpleObjectProperty<BigDecimal>();
		importe = new SimpleObjectProperty<BigDecimal>();
		importeTotalFinal = new SimpleObjectProperty<BigDecimal>(cupon.getImporteTotalAhorrado().negate());
		descuento = new SimpleObjectProperty<BigDecimal>();
		vendedor = new SimpleStringProperty("");
		this.cupon = true;
		this.editable = false;
	}

	public String getArticulo() {
		return articulo.get();
	}

	public SimpleStringProperty articuloProperty() {
		return articulo;
	}

	public SimpleStringProperty getArtProperty() {
		return articulo;
	}

	public SimpleStringProperty getDescripcionProperty() {
		return descripcion;
	}

	public String getDesglose1() {
		return desglose1.get();
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

	public SimpleObjectProperty<BigDecimal> getPvpConDtoProperty() {
		return pvpConDto;
	}

	public SimpleObjectProperty<BigDecimal> getCantidadProperty() {
		return cantidad;
	}

	public SimpleObjectProperty<BigDecimal> getImporteProperty() {
		return importe;
	}

	public SimpleObjectProperty<BigDecimal> getImporteTotalFinalProperty() {
		return importeTotalFinal;
	}

	public SimpleObjectProperty<BigDecimal> getDescuentoProperty() {
		return descuento;
	}

	public Integer getIdLinea() {
		return idLinea;
	}

	public Boolean isCupon() {
		return cupon;
	}

	public boolean isLineaDocAjeno() {
		return lineaDocAjeno;
	}

	public SimpleStringProperty getVendedorProperty() {
		return vendedor;
	}

	public void setArticulo(String articulo) {
		this.articulo.set(articulo);
	}

	public void setDescripcion(String descripcion) {
		this.descripcion.set(descripcion);
	}

	public void setDesglose1(String desglose1) {
		this.desglose1.set(desglose1);
	}

	public void setDesglose2(String desglose2) {
		this.desglose2.set(desglose2);
	}

	public void setPvp(BigDecimal pvp) {
		this.pvp.set(pvp);
	}

	public void setPvpConDto(BigDecimal pvpConDto) {
		this.pvpConDto.set(pvpConDto);
	}

	public void setCantidad(BigDecimal cantidad) {
		this.cantidad.set(cantidad);
	}

	public void setImporte(BigDecimal importe) {
		this.importe.set(importe);
	}

	public void setImporteTotalFinal(BigDecimal importeTotalFinal) {
		this.importeTotalFinal.set(importeTotalFinal);
	}

	public void setDescuento(BigDecimal descuento) {
		this.descuento.set(descuento);
	}

	public void setIdLinea(Integer idLinea) {
		this.idLinea = idLinea;
	}

	public void setCupon(Boolean cupon) {
		this.cupon = cupon;
	}

	public void setLineaDocAjeno(boolean lineaDocAjeno) {
		this.lineaDocAjeno = lineaDocAjeno;
	}

	public void setVendedor(String vendedor) {
		this.vendedor.set(vendedor);
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(final boolean editable) {
		this.editable = editable;
	}
}
