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


package com.comerzzia.pos.gui.ventas.devoluciones;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.validation.FormularioGui;

@Component
@Scope("prototype")
public class FormularioConsultaTicketBean extends FormularioGui{
    
    @Size(max = 4, message = "El campo 'Código de tienda' no puede tener más de {max} dígitos.")
    @NotEmpty (message = "El campo 'Código de tienda' no puede estar vacío.")
    String codTienda;
    
    @NotEmpty(message = "El campo 'Código de ticket' no puede estar vacío.")
    String codOperacion;
    
    @NotEmpty (message = "El campo 'Código de caja' no puede estar vacío.")
    @Size(max = 4, message = "El campo 'Código de caja' no puede tener más de {max} dígitos.")
    String codCaja;
    
    @NotEmpty(message = "El campo 'Tipo de documento' no puede estar vacío.")
    @Size(max = 4, message = "El campo 'Código de documento' no puede tener más de {max} dígitos.")
    String codDoc;
    
    public FormularioConsultaTicketBean(){
        
    }
    
    public FormularioConsultaTicketBean(String codTienda, String codCaja, String codOperacion, String tipoDoc){
        
        this.codTienda = codTienda;
        this.codOperacion = codOperacion;
        this.codCaja = codCaja;
        this.codDoc = tipoDoc;
    }

    public String getCodTienda() {
        return codTienda;
    }

    public void setCodTienda(String codTienda) {
        this.codTienda = codTienda!=null?codTienda.trim():codTienda;
    }

    public String getCodOperacion() {
        return codOperacion;
    }

    public void setCodOperacion(String codOperacion) {
        this.codOperacion = codOperacion!=null?codOperacion.trim():codOperacion;
    }

    public String getCodCaja() {
        return codCaja;
    }

    public void setCodCaja(String codCaja) {
        this.codCaja = codCaja!=null?codCaja.trim():codCaja;
    }

    public String getCodDoc() {
        return codDoc;
    }

    public void setCodDoc(String codDoc) {
        this.codDoc = codDoc!=null?codDoc.trim():codDoc;
    }
    
    @Override
    public void limpiarFormulario() {
        
        codTienda = "";
        codOperacion = "";
        codCaja = "";
        codDoc = "";
    }
    
}
