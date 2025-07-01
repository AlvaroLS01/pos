
package com.comerzzia.pos.gui.sales.layaway.items.verPagos;

import java.math.BigDecimal;
import java.util.Date;

import com.comerzzia.omnichannel.facade.model.cashjournal.CashJournalLine;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;


public class VerPagosApartadoGui {
	
	private SimpleObjectProperty<Date> fechaPago;
	
	private SimpleObjectProperty<BigDecimal> importe;
	
	private CashJournalLine movimiento;
	
	private SimpleStringProperty medioPago;
	
	public VerPagosApartadoGui(CashJournalLine movimiento){
		
		this.fechaPago = new SimpleObjectProperty<Date>();
		fechaPago.setValue(movimiento.getCashJournalDate());
		
		this.importe = new SimpleObjectProperty<BigDecimal>();
		importe.setValue(movimiento.getInput());
		
		this.medioPago = new SimpleStringProperty();
		medioPago.setValue(movimiento.getPaymentMethodCode());
		
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
	
	public CashJournalLine getMovimiento() {
		return movimiento;
	}
	
	public SimpleStringProperty getMedioPagoProperty(){
		return medioPago;
	}
}
