
package com.comerzzia.pos.gui.sales.layaway;

import java.math.BigDecimal;
import java.util.Date;

import com.comerzzia.omnichannel.facade.model.deprecated.apartados.ApartadosCabeceraBean;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;


public class LineaApartadoDto  {
	
    private SimpleObjectProperty<Long> numApartado;
    private SimpleStringProperty cliente;
    private SimpleObjectProperty<Date> fecha;
    private SimpleObjectProperty<BigDecimal> importe;
    private SimpleObjectProperty<BigDecimal> saldoCliente;
    private ApartadosCabeceraBean apartado;
    private short estado;    
	
	public LineaApartadoDto(ApartadosCabeceraBean apartado){
		
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
