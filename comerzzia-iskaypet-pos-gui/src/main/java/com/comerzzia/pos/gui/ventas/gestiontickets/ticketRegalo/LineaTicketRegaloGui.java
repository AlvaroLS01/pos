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
package com.comerzzia.pos.gui.ventas.gestiontickets.ticketRegalo;

import java.math.BigDecimal;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;


public class LineaTicketRegaloGui {

private SimpleStringProperty descArticulo;
    
    private SimpleObjectProperty<BigDecimal> cantidad;
    
    private SimpleStringProperty codArticulo;
    
    private SimpleStringProperty desglose1;
    
    private SimpleStringProperty desglose2;
    
    private int idLinea;
    
    private SimpleBooleanProperty lineaSelec;
    
    public LineaTicketRegaloGui(String desArticulo, BigDecimal cantidad, int idLinea, String codArticulo, String desglose1, String desglose2){
        
        descArticulo = new SimpleStringProperty(desArticulo);
        this.cantidad = new SimpleObjectProperty(cantidad);
        this.codArticulo = new SimpleStringProperty(codArticulo);
        this.lineaSelec = new SimpleBooleanProperty(false);
        this.idLinea = idLinea;
        this.desglose1 = new SimpleStringProperty(desglose1);
        this.desglose2 = new SimpleStringProperty(desglose2);
    }
    
    public String getCodArticuloString(){
        return codArticulo.getValue();
    }
    
    public SimpleStringProperty getCodArticulo(){
        return codArticulo;
    }
    
    public int getIdLinea(){
        return idLinea;
    }
    
    public BigDecimal getCantidadBigDecimal(){
        return cantidad.getValue();
    }
    
    public SimpleObjectProperty<BigDecimal> getCantidad(){
        return cantidad;
    }
    
    public SimpleStringProperty getDesArticulo(){
        return descArticulo;
    }
    
    public String getDesArticuloString(){
        return descArticulo.getValue();
    }
    
    public SimpleBooleanProperty lineaSelecProperty(){
        return lineaSelec;
    }
    
    public boolean isLineaSelec(){
        return lineaSelec.getValue();
    }
    
    public void setLineaSelec(boolean lineaSelec){
        this.lineaSelec.setValue(lineaSelec);
    }
    
    public String getDesglose1String(){
        return desglose1.getValue();
    }
    
    public SimpleStringProperty getDesglose1(){
        return desglose1;
    }
    
    public String getDesglose2String(){
        return desglose2.getValue();
    }
    
    public SimpleStringProperty getDesglose2(){
        return desglose2;
    }
}
