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

package com.comerzzia.pos.gui.ventas.tickets.articulos.busquedas;

import java.math.BigDecimal;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import com.comerzzia.pos.persistence.articulos.buscar.ArticuloBuscarBean;


public class LineaResultadoBusqGui {

    private SimpleObjectProperty<BigDecimal> pvp;
    private SimpleStringProperty codArticulo;
    private SimpleStringProperty descripcion;
    private SimpleStringProperty desglose1;
    private SimpleStringProperty desglose2;
    private SimpleObjectProperty<BigDecimal> precio;

    public LineaResultadoBusqGui(ArticuloBuscarBean articuloBuscar) {

        this.descripcion = new SimpleStringProperty(articuloBuscar.getDesArticulo());
        this.codArticulo= new SimpleStringProperty(articuloBuscar.getCodArticulo());
        this.pvp= new SimpleObjectProperty(articuloBuscar.getPrecioTotal());
        this.desglose1 = new SimpleStringProperty(articuloBuscar.getValorDesglose1());
        this.desglose2 = new SimpleStringProperty(articuloBuscar.getValorDesglose2());
        this.precio= new SimpleObjectProperty(articuloBuscar.getPrecio());
        
    }

    public String getCodArticulo() {
        return codArticulo.getValue();
    }

    public void setArticulo(String articulo) {
        this.codArticulo.setValue(articulo);
    }

    public SimpleStringProperty getArtProperty() {
        return codArticulo;
    }

    public String getDescripcion() {
        return descripcion.getValue();
    }

    public void setDescripcion(String descripcion) {
        this.descripcion.setValue(descripcion);
    }

    public SimpleStringProperty getDescripcionProperty() {
        return descripcion;
    }
    
    public BigDecimal getPvp(){
        return pvp.getValue();
    }
    
    public void setPvp (BigDecimal pvp){
        this.pvp.setValue(pvp);
    }
    
    public SimpleObjectProperty<BigDecimal> getPvpProperty(){
        return pvp;
    }

    public SimpleStringProperty getDesglose1Property() {
        return desglose1;
    }

    public SimpleStringProperty getDesglose2Property() {
        return desglose2;
    }
    
    public String getDesglose1() {
        return desglose1.getValue();
    }

    public String getDesglose2() {
        return desglose2.getValue();
    }
    
    public BigDecimal getPrecio(){
        return precio.getValue();
    }
    
    public void setPrecio(BigDecimal precio){
        this.precio.setValue(precio);
    }
    
    public SimpleObjectProperty<BigDecimal> getPrecioProperty(){
        return precio;
    }
}
