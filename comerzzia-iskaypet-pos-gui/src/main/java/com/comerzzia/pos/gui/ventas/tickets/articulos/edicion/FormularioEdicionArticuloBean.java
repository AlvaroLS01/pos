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

package com.comerzzia.pos.gui.ventas.tickets.articulos.edicion;

import java.math.BigDecimal;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.validation.FormularioGui;
import com.comerzzia.pos.core.gui.validation.validators.number.esnumerico.EsNumerico;
import com.comerzzia.pos.persistence.core.usuarios.UsuarioBean;
import com.comerzzia.pos.util.format.FormatUtil;

@Component
@Scope("prototype")
public class FormularioEdicionArticuloBean extends FormularioGui {

	@Size(min = 1, max = 45, message = "La longitud del campo debe estar entre {min} y {max} caracteres.")  
	protected String desArticulo;
	
    @NotEmpty(message = "La cantidad no puede ser nula")
    @EsNumerico(decimales = 3,message = "El campo cantidad no es un número válido.")
    protected String cantidad;
    
    @NotEmpty(message = "El precio no puede ser nulo")
    @EsNumerico(esImporte = true, message = "El campo precio no es un número válido.")
    protected String precioFinal;
    
    @NotEmpty(message = "El precio no puede ser nulo")
	@EsNumerico(decimales = 4, message = "El campo precio no es un número válido.")
	protected String precioFinalProfesional;

    @EsNumerico( decimales = 2, message = "El campo descuento no es un número válido.")
    protected String descuento;
    
    protected UsuarioBean vendedor;
    
  
	// Los campos anteriores no necesarios para validación. En el caso de este formulario necesitamos saber si algunas propiedades han cambiado
    protected SimpleStringProperty precioUnitarioSP;
    protected SimpleStringProperty descuentoSP;
    protected SimpleStringProperty precioFinalSP;
    protected SimpleObjectProperty vendedorSP;
    protected SimpleStringProperty desArticuloSP;

    public FormularioEdicionArticuloBean() {
        descuentoSP = new SimpleStringProperty();
        precioUnitarioSP = new SimpleStringProperty();
        precioFinalSP = new SimpleStringProperty();
        vendedorSP = new SimpleObjectProperty();
        desArticuloSP = new SimpleStringProperty();
    }

    public FormularioEdicionArticuloBean(String desArticulo, String cantidad, String descuento, String precioFinal, String precioUnitario, UsuarioBean vendedor, String importeProfesional) {
    	this();
        this.descuento = descuento;
        this.cantidad = cantidad;
        this.precioFinal = precioFinal;
        this.precioFinalProfesional = importeProfesional;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setPrecioFinalSP(SimpleStringProperty precioFinalSP) {
        this.precioFinalSP = precioFinalSP;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getDescuento() {
        return descuento;
    }
    
    public void setDescuento(String descuento) {
    	this.descuento = descuento;
    	descuentoSP.setValue(descuento);
    }

    public String getPrecioFinal() {
        return precioFinal;
    }

    public void setPrecioFinal(String precioFinal) {
        this.precioFinal = precioFinal;
        precioFinalSP.setValue(precioFinal);
    }

    @Override
    public void limpiarFormulario() {
        cantidad = "";
        descuento = "";
        precioFinal = "";
        precioFinalProfesional = "";
    }

    public BigDecimal getPrecioFinalAsBD() {
        return FormatUtil.getInstance().desformateaBigDecimal(precioFinal);
    }
    
    public BigDecimal getCantidadAsBD() {
        return FormatUtil.getInstance().desformateaBigDecimal(cantidad,3);
    }
    
    public BigDecimal getDescuentoAsBD() {
        return FormatUtil.getInstance().desformateaBigDecimal(descuento);
    }
    
    // Getters y Setters de propiedades observables para actualizar el formulario
    // Las usaremos desde los setters de los parámetros no observables.
    public SimpleStringProperty getDescuentoSP() {
    	return descuentoSP;
    }
    
    public void setDescuentoSP(SimpleStringProperty descuentoSP) {
    	this.descuentoSP = descuentoSP;
    }
    
    public SimpleStringProperty getPrecioFinalSP() {
        return precioFinalSP;
    }
    
    /**
     * @return the vendedor
     */
    public UsuarioBean getVendedor() {
        return vendedor;
    }

    /**
     * @param vendedor the vendedor to set
     */
    public void setVendedor(UsuarioBean vendedor) {
        this.vendedor = vendedor;        
        vendedorSP.setValue(vendedor);
    }
    
    public String getDesArticulo() {
    	return desArticulo;
    }
    
    public void setDesArticulo(String desArticulo) {
        this.desArticulo = desArticulo!=null?desArticulo.trim():desArticulo;
        desArticuloSP.setValue(desArticulo!=null?desArticulo.trim():desArticulo);
    }
    
    public String getPrecioFinalProfesional() {
        return precioFinalProfesional;
    }

    public void setPrecioFinalProfesional(String precioFinalProfesional) {
        this.precioFinalProfesional = precioFinalProfesional;
        precioFinalSP.setValue(precioFinalProfesional);
    }
}
