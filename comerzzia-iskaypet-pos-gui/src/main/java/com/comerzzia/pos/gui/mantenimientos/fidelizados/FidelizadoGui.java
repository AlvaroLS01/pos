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

package com.comerzzia.pos.gui.mantenimientos.fidelizados;

import com.comerzzia.api.model.loyalty.FidelizadoBean;

import javafx.beans.property.SimpleStringProperty;


public class FidelizadoGui {
    
    private SimpleStringProperty descripcion;
    private SimpleStringProperty cif;
    private SimpleStringProperty poblacion;
    private SimpleStringProperty provincia;

    private FidelizadoBean fidelizado;
    
    /**
     * Constructor de recuento
     * @param medioPago
     * @param cantidad
     * @param moneda 
     */
    public FidelizadoGui (FidelizadoBean fidelizado){
        this.fidelizado = fidelizado;
        this.descripcion = new SimpleStringProperty(fidelizado.getNombreCompleto()== null? "":fidelizado.getNombreCompleto());
        this.cif = new SimpleStringProperty(fidelizado.getDocumento()== null? "":fidelizado.getDocumento());
        this.poblacion = new SimpleStringProperty(fidelizado.getPoblacion()== null? "":fidelizado.getPoblacion());
        this.provincia = new SimpleStringProperty(fidelizado.getProvincia()== null? "":fidelizado.getProvincia());
    }

    /**
     * @return la forma de pago
     */
    public String getDescripcion() {
        return descripcion.get();
    }
    
    /**
     * @return la forma de pago
     */
    public SimpleStringProperty descripcionProperty() {
        return descripcion;
    }
        /**
     * @return la forma de pago
     */
    public String getCif() {
        return cif.get();
    }
    
    /**
     * @return la forma de pago
     */
    public SimpleStringProperty cifProperty() {
        return cif;
    }
        /**
     * @return la forma de pago
     */
    public String getPoblacion() {
        return poblacion.get();
    }
    
    /**
     * @return la forma de pago
     */
    public SimpleStringProperty poblacionProperty() {
        return poblacion;
    }
        /**
     * @return la forma de pago
     */
    public String getProvincia() {
        return provincia.getValue();
    }
    
    /**
     * @return la forma de pago
     */
    public SimpleStringProperty provinciaProperty() {
        return provincia;
    }
    
    public FidelizadoBean getFidelizado(){
    	return fidelizado;
    }
    
}
