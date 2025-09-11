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
package com.comerzzia.pos.gui.ventas.apartados;

import java.math.BigDecimal;
import java.util.Date;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import com.comerzzia.pos.persistence.apartados.ApartadosCabeceraBean;


public class LineaApartadoGui  {
	
    private SimpleObjectProperty<Long> numApartado;
    private SimpleStringProperty cliente;
    private SimpleObjectProperty<Date> fecha;
    private SimpleObjectProperty<BigDecimal> importe;
    private SimpleObjectProperty<BigDecimal> saldoCliente;
    private ApartadosCabeceraBean apartado;
    private short estado;    
	
	public LineaApartadoGui(ApartadosCabeceraBean apartado){
		
		numApartado = new SimpleObjectProperty<Long>();
		numApartado.setValue(apartado.getIdApartado()!=null? apartado.getIdApartado(): 0);
		
		cliente = new SimpleStringProperty();
		cliente.setValue(apartado.getDesCliente());
		
		importe = new SimpleObjectProperty<BigDecimal>();
		importe.setValue(apartado.getImporteTotalApartado());
		
		saldoCliente = new SimpleObjectProperty<BigDecimal>();
		saldoCliente.setValue(apartado.getSaldoCliente());
		
		fecha = new SimpleObjectProperty<Date>();
		fecha.setValue(apartado.getFechaApartado());
		
		estado = apartado.getEstadoApartado();
				
		this.apartado = apartado;
	}

	public SimpleObjectProperty<Long> getNumApartadoProperty() {
		return numApartado;
	}

	public SimpleStringProperty getClienteProperty() {
		return cliente;
	}

	public SimpleObjectProperty<BigDecimal> getImporteProperty() {
		return importe;
	}

	public SimpleObjectProperty<BigDecimal> getSaldoClienteProperty() {
		return saldoCliente;
	}
	
	public SimpleObjectProperty<Date> getFechaProperty() {
		return fecha;
	}

	public void setEstado(short estado){
		this.estado = estado;
	}
	
	public short getEstado(){
		return estado;
	}
	
	public ApartadosCabeceraBean getApartado(){
		return apartado;
	}
}
