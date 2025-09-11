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

package com.comerzzia.pos.gui.ventas.cajas.recuentos;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import com.comerzzia.pos.persistence.cajas.recuentos.CajaLineaRecuentoBean;
import com.comerzzia.pos.persistence.mediosPagos.MedioPagoBean;
import com.comerzzia.pos.services.mediospagos.MediosPagosService;
import com.comerzzia.pos.util.config.SpringContext;


public class RecuentoCajaGui {
    
    private final SimpleStringProperty formaPago;
    private final SimpleObjectProperty<Integer> cantidad;
    private final SimpleObjectProperty<String> moneda;
    private final SimpleObjectProperty<String> total;
    
    private final CajaLineaRecuentoBean lineaRecuento;
        
    /**
     * Constructor de recuento
     * @param lineaRecuento
     * @param estado
     */
    public RecuentoCajaGui (CajaLineaRecuentoBean lineaRecuento){
    	MediosPagosService mediosPagosService = SpringContext.getBean(MediosPagosService.class);
        MedioPagoBean medioPago = mediosPagosService.getMedioPago(lineaRecuento.getCodMedioPago());
        this.formaPago = new SimpleStringProperty(medioPago.getDesMedioPago());
        this.cantidad = new SimpleObjectProperty<>(lineaRecuento.getCantidad());
        this.moneda = new SimpleObjectProperty<>(lineaRecuento.getValorAsString());        
        this.total = new SimpleObjectProperty<>(lineaRecuento.getTotalAsString());
        
        this.lineaRecuento = lineaRecuento;
    }

    /**
     * @return la forma de pago
     */
    public SimpleStringProperty getFormaPagoProperty() {
        return formaPago;
    }
    
    
    /**
     * @return cantidad
     */
    public SimpleObjectProperty<Integer> getCantidadProperty() {
        return cantidad;
    }
        
    /**
     * @return moneda
     */
    public SimpleObjectProperty<String> getMonedaProperty() {
        return moneda;
    }
    
    
     /**
     * @return total
     */
    public SimpleObjectProperty<String> getTotalProperty() {
        return total;
    }

    public CajaLineaRecuentoBean getLineaRecuento() {
        return lineaRecuento;
    }
    
    
}
