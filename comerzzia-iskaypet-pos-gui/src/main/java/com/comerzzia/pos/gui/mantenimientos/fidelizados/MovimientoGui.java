package com.comerzzia.pos.gui.mantenimientos.fidelizados;

import java.math.BigDecimal;
import java.util.Date;

import com.comerzzia.api.model.loyalty.MovimientoBean;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class MovimientoGui {
	
	private SimpleObjectProperty<Date> fecha;
	private SimpleStringProperty concepto;
	private SimpleObjectProperty<BigDecimal> entrada, salida;
	private SimpleObjectProperty<String> estado;
	
	public MovimientoGui(MovimientoBean movimiento){
		this.fecha = new SimpleObjectProperty<Date>(movimiento.getFecha());
		this.concepto = new SimpleStringProperty(movimiento.getConcepto()== null? "":movimiento.getConcepto());
		BigDecimal entradaBD = new BigDecimal(movimiento.getEntrada());
		BigDecimal salidaBD = new BigDecimal(movimiento.getSalida());
		this.entrada = new SimpleObjectProperty<BigDecimal>(BigDecimal.ZERO.equals(entradaBD) ? null : entradaBD);
		this.salida = new SimpleObjectProperty<BigDecimal>(BigDecimal.ZERO.equals(salidaBD) ? null : salidaBD);
		
		if(MovimientoBean.MOVIMIENTO_DEFINITIVO.equals(movimiento.getEstadoMovimiento())) {
			this.estado = new SimpleObjectProperty<String>(I18N.getTexto("Definitivo"));
		}
		else if(MovimientoBean.MOVIMIENTO_PROVISIONAL.equals(movimiento.getEstadoMovimiento())) {
			this.estado = new SimpleObjectProperty<String>(I18N.getTexto("Provisional"));
		}
		else if(MovimientoBean.MOVIMIENTO_ANULADO.equals(movimiento.getEstadoMovimiento())) {
			this.estado = new SimpleObjectProperty<String>(I18N.getTexto("Anulado"));
		}
	}
	
	public SimpleStringProperty conceptoProperty() {
        return concepto;
    }
    
    public String getConcepto(){
    	return concepto.get();
    }
    
    public SimpleObjectProperty<Date> fechaProperty(){
    	return fecha;
    }
    
    public Date getFecha(){
    	return fecha.get();
    }
    
    public SimpleObjectProperty<BigDecimal> entradaProperty(){
    	return entrada;
    }
    
    public BigDecimal getEntrada(){
    	return entrada.get();
    }
    
    public SimpleObjectProperty<BigDecimal> salidaProperty(){
    	return salida;
    }
    
    public BigDecimal getSalida(){
    	return salida.get();
    }

	public String getEstado() {
		return estado.get();
	}
}
