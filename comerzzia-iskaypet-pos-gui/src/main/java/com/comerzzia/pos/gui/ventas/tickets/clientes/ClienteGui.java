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

package com.comerzzia.pos.gui.ventas.tickets.clientes;

import javafx.beans.property.SimpleStringProperty;

import com.comerzzia.pos.persistence.clientes.ClienteBean;


public class ClienteGui {
    
    private SimpleStringProperty descripcion;
    private SimpleStringProperty cif;
    private SimpleStringProperty poblacion;
    private SimpleStringProperty provincia;

    private ClienteBean cliente;
    
    /**
     * Constructor de recuento
     * @param medioPago
     * @param cantidad
     * @param moneda 
     */
    public ClienteGui (ClienteBean cliente){
        this.cliente = cliente;
        this.descripcion = new SimpleStringProperty(cliente.getDesCliente()== null? "":cliente.getDesCliente());
        this.cif = new SimpleStringProperty(cliente.getCif()== null? "":cliente.getCif());
        this.poblacion = new SimpleStringProperty(cliente.getPoblacion()== null? "":cliente.getPoblacion());
        this.provincia = new SimpleStringProperty(cliente.getProvincia()== null? "":cliente.getProvincia());
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
    
    public ClienteBean getCliente(){
    	return cliente;
    }
    
}
