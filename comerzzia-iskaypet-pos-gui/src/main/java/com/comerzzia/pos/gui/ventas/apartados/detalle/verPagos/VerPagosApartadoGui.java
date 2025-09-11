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
package com.comerzzia.pos.gui.ventas.apartados.detalle.verPagos;

import java.math.BigDecimal;
import java.util.Date;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import com.comerzzia.pos.persistence.cajas.movimientos.CajaMovimientoBean;


public class VerPagosApartadoGui {
	
	private SimpleObjectProperty<Date> fechaPago;
	
	private SimpleObjectProperty<BigDecimal> importe;
	
	private CajaMovimientoBean movimiento;
	
	private SimpleStringProperty medioPago;
	
	public VerPagosApartadoGui(CajaMovimientoBean movimiento){
		
		this.fechaPago = new SimpleObjectProperty<Date>();
		fechaPago.setValue(movimiento.getFecha());
		
		this.importe = new SimpleObjectProperty<BigDecimal>();
		importe.setValue(movimiento.getCargo());
		
		this.medioPago = new SimpleStringProperty();
		medioPago.setValue(movimiento.getDesMedioPago());
		
		this.movimiento = movimiento;
	}

	public SimpleObjectProperty<Date> getFechaPagoProperty() {
		return fechaPago;
	}

	public BigDecimal getImporte() {
		return importe.getValue();
	}

	public Date getFechaPago() {
		return fechaPago.getValue();
	}

	public SimpleObjectProperty<BigDecimal> getImporteProperty() {
		return importe;
	}
	
	public CajaMovimientoBean getMovimiento() {
		return movimiento;
	}
	
	public SimpleStringProperty getMedioPagoProperty(){
		return medioPago;
	}
}
