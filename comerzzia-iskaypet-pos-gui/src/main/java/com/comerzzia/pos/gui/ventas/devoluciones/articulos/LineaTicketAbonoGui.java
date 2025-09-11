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


package com.comerzzia.pos.gui.ventas.devoluciones.articulos;

import java.math.BigDecimal;

import javafx.beans.property.SimpleObjectProperty;

import com.comerzzia.pos.gui.ventas.tickets.articulos.LineaTicketGui;
import com.comerzzia.pos.services.ticket.lineas.LineaTicketAbstract;
import com.comerzzia.pos.util.format.FormatUtil;


public class LineaTicketAbonoGui extends LineaTicketGui{

    private SimpleObjectProperty<BigDecimal> cantidadDevuelta;
    
    private SimpleObjectProperty<BigDecimal> cantidadADevolver;
    
    public LineaTicketAbonoGui(LineaTicketAbstract linea) {
        super(linea);
        cantidadADevolver = new SimpleObjectProperty(linea.getCantidadADevolver());
        
        
        cantidadDevuelta = new SimpleObjectProperty(linea.getCantidadDevuelta());
    }

    public SimpleObjectProperty<BigDecimal> getCantidadDevuelta() {
        return cantidadDevuelta;
    }

    public void setCantidadDevuelta(SimpleObjectProperty<BigDecimal> cantidadDevuelta) {
        this.cantidadDevuelta = cantidadDevuelta;
    }

    public SimpleObjectProperty<BigDecimal> getCantidadADevolver() {
        return cantidadADevolver;
    }    
    
    public String getCantidadADevolverAsString() {
        return FormatUtil.getInstance().formateaNumero(cantidadADevolver.getValue().negate());
    } 
    
    public void setCantidadADevolverAsString(String cantidad) {
        BigDecimal cantidadBigDecimal = FormatUtil.getInstance().desformateaBigDecimal(cantidad,3);
        cantidadADevolver.setValue(cantidadBigDecimal);
    }
    
}
