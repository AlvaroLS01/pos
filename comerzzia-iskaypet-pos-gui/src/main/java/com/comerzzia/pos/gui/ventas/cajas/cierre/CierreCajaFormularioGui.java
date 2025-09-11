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

package com.comerzzia.pos.gui.ventas.cajas.cierre;

import java.util.Date;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.validation.FormularioGui;
import com.comerzzia.pos.core.gui.validation.validators.date.EsFechaValida;
import com.comerzzia.pos.util.format.FormatUtil;

@Component
@Scope("prototype")
public class CierreCajaFormularioGui extends FormularioGui {

    @NotEmpty(message = "Ha de indicar una fecha de cierre")
    @EsFechaValida(pasada = true, futura = true, message = "La fecha de cierre introducida no es válida", esFechaHora = false)
    private String fechaCierre;

    private Date dateCierre;
    
    @Override
    public void limpiarFormulario() {
        fechaCierre = "";
    }

    public void setFechaCierre(String fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

	public Date getDateCierre() {
		if(dateCierre == null){
			return FormatUtil.getInstance().desformateaFecha(fechaCierre);
		}else{
			return dateCierre;
		}
	}

	public void setDateCierre(Date dateCierre) {
		this.dateCierre = dateCierre;
	}

    
    
}
