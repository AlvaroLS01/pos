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


package com.comerzzia.pos.gui.ventas.cajas.apertura;


import java.math.BigDecimal;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.validation.FormularioGui;
import com.comerzzia.pos.core.gui.validation.validators.date.EsFechaValida;
import com.comerzzia.pos.core.gui.validation.validators.number.esnumerico.EsNumerico;
import com.comerzzia.pos.util.format.FormatUtil;

@Component
@Scope("prototype")
public class AperturaCajaFormularioBean extends FormularioGui{   
    //@Past(message = "La fecha de apertura introducida no es válida")
    @NotEmpty(message = "Ha de indicar una fecha de apertura")
    @EsFechaValida(esFechaHora = false)
    String fechaApertura;
    
    @EsNumerico(esImporte = true)
    @Size(min = 1, max = 10, message = "El saldo ha de tener un valor positivo válido")
    String saldo;
    
    public AperturaCajaFormularioBean() {
    }

    public AperturaCajaFormularioBean(String fechaApertura, String saldo) {
        this.fechaApertura = fechaApertura;
        this.saldo = saldo;
    }

    @Override
    public void limpiarFormulario() {
        this.fechaApertura = "";
        this.saldo = "";
    }

    public String getFechaApertura() {
        return fechaApertura;
    }

    public void setFechaApertura(String fechaApertura) {
        this.fechaApertura = fechaApertura;
    }

    public String getSaldo() {
        return saldo;
    }

    public BigDecimal getSaldoAsBigDecimal() {
        return FormatUtil.getInstance().desformateaBigDecimal(saldo,2);
    }

    public void setSaldo(String saldo) {
        this.saldo = saldo;
    }
    
    
    
}
