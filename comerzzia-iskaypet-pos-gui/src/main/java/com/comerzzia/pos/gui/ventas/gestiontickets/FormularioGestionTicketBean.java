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

package com.comerzzia.pos.gui.ventas.gestiontickets;

import java.util.Date;

import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.validation.FormularioGui;
import com.comerzzia.pos.core.gui.validation.validators.date.EsFechaValida;
import com.comerzzia.pos.core.gui.validation.validators.number.esnumerico.EsNumerico;
import com.comerzzia.pos.util.format.FormatUtil;

@Component
@Scope("prototype")
public class FormularioGestionTicketBean extends FormularioGui {

    @Size(min = 0, max = 4, message = "La longitud del código no puede tener más de {max} dígitos.")
    private String codCaja;

    @Size(min = 0, max = 10, message = "La longitud del código no puede tener más de {max} dígitos.")
    @EsNumerico(decimales = 0, message = "El código del ticket no es válido.")
    private String idTicket;

    @EsFechaValida(pasada = true, futura = true, message = "La fecha introducida no tiene un formato correcto.")
    private String fecha;
    
    @Size(min = 0, max = 10, message = "La longitud del código no puede tener más de {max} dígitos.")
    private String idDoc;

    public FormularioGestionTicketBean() {
    }

    public FormularioGestionTicketBean(String codCaja, String idTicket, String fecha, String idDoc) {

        this.codCaja = codCaja;
        this.idTicket = idTicket;
        this.fecha = fecha;
        this.idDoc = idDoc;
    }

    public String getCodCaja() {
        return codCaja;
    }

    public void setCodCaja(String codCaja) {
        this.codCaja = codCaja!=null?(StringUtils.isNotBlank(codCaja.trim())?codCaja.trim():null):codCaja;
    }

    public String getCodTicket() {
        return idTicket;
    }

    public void setCodTicket(String codTicket) {
        this.idTicket = codTicket!=null?(StringUtils.isNotBlank(codTicket.trim())?codTicket.trim():null):codTicket;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha!=null?(StringUtils.isNotBlank(fecha.trim())?fecha.trim():null):fecha;
    }

    public String getIdDoc() {
        return idDoc;
    }

    public void setIdDoc(String idDoc) {
        this.idDoc = idDoc!=null?StringUtils.isNotBlank(idDoc.trim())?idDoc.trim():null:idDoc;
    }

    @Override
    public void limpiarFormulario() {

        codCaja = "";
        idTicket = "";
        fecha = "";
        idDoc = "";
    }
    
    public Date getFechaAsDate() {
        Date res = null;
        if (StringUtils.isNotBlank(fecha)){
            res =  FormatUtil.getInstance().desformateaFecha(fecha);
        }
        return res;
    }
    
    public Long getIdTicketAsLong() {
        Long res = null;
        if (StringUtils.isNotBlank(idTicket)){
            res =  FormatUtil.getInstance().desformateaLong(idTicket);
            
        }
        return res;
    }
}
