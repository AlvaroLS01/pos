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
package com.comerzzia.iskaypet.pos.gui.ventas.gestiontickets.cupones;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

import com.comerzzia.pos.services.ticket.cupones.CuponEmitidoTicket;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;


public class IskaypetLineaCuponGui {

	private SimpleStringProperty titulo;
	
	private SimpleStringProperty codigo;
    
    private SimpleObjectProperty<BigDecimal> importe;
    
    private SimpleStringProperty fechaInicio;
    
    private SimpleStringProperty fechaFin;
    
    
    private SimpleBooleanProperty lineaSelec;
    
    private CuponEmitidoTicket cupon;
    
    
  public IskaypetLineaCuponGui(CuponEmitidoTicket cupon){
        this.cupon = cupon;
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        
    	this.titulo = new SimpleStringProperty(cupon.getTituloCupon());
    	this.codigo = new SimpleStringProperty(cupon.getCodigoCupon());
    	this.fechaInicio = new SimpleStringProperty(format.format(cupon.getFechaInicio()));
    	this.fechaFin = new SimpleStringProperty(format.format(cupon.getFechaFin()));
    	this.importe = new SimpleObjectProperty<>(cupon.getImporteCupon());
    	this.lineaSelec = new SimpleBooleanProperty(false);
  
    }
    
    
    public String getTituloString(){
        return titulo.getValue();
    }
    
    public SimpleStringProperty getTitulo(){
        return titulo;
    }
    
    public String getCodigoString(){
        return codigo.getValue();
    }
    
    public SimpleStringProperty getCodigo(){
        return codigo;
    }
    
    public SimpleObjectProperty<BigDecimal> getImporte(){
        return importe;
    }
    
    public SimpleStringProperty getFechaInicio(){
        return fechaInicio;
    }
    
    public String getFechaInicioString(){
        return fechaInicio.getValue();
    }
    
    public SimpleStringProperty getFechaFin(){
        return fechaFin;
    }
    
    public String getFechaFinString(){
        return fechaFin.getValue();
    }
    
    public SimpleBooleanProperty lineaSelecProperty(){
        return lineaSelec;
    }
    
    public void setLineaSelec(boolean lineaSelec){
        this.lineaSelec.setValue(lineaSelec);
    }
    
    
    public boolean isLineaSelec(){
        return lineaSelec.getValue();
    }



	public CuponEmitidoTicket getCupon() {
		return cupon;
	}



	public void setCupon(CuponEmitidoTicket cupon) {
		this.cupon = cupon;
	}
    
    
}
