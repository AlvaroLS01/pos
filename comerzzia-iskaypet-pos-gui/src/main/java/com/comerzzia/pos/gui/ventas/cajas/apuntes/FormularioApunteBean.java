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


package com.comerzzia.pos.gui.ventas.cajas.apuntes;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.validation.FormularioGui;
import com.comerzzia.pos.core.gui.validation.validators.number.esnumerico.EsNumerico;
import com.comerzzia.pos.persistence.cajas.conceptos.CajaConceptoBean;
import com.comerzzia.pos.util.format.FormatUtil;

@Component
@Scope("prototype")
public class FormularioApunteBean extends FormularioGui{

    @Size(min=0, max = 60, message = "El documento no puede superar los {max} caracteres")
    protected String documento;
    
    @NotNull (message ="Campo requerido")
    protected CajaConceptoBean concepto;
    
    @EsNumerico(esImporte = true, message = "El importe introducido no es válido")
    @NotEmpty (message ="Campo requerido")
    protected String importe;
    
    @Size(min=0, max = 255, message = "La descripción del concepto no puede superar los {max} caracteres")
    @NotEmpty (message ="Campo requerido")
    protected String descripcion;
    
    public FormularioApunteBean() {

    }
    
    public FormularioApunteBean(String documento, String importe, CajaConceptoBean concepto) {
        this.documento = documento;
        this.importe = importe;
        this.concepto = concepto;
        this.descripcion = concepto.getDesConceptoMovimiento();
    }
    
    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getImporte() {
        return importe;
    }

    public void setImporte(String importe) {
        this.importe = importe;
    }
    
    @Override
    public void limpiarFormulario() {
        documento = "";
        importe = "";
        concepto = null;
        descripcion = "";
    }

    public CajaConceptoBean getConcepto() {
        return concepto;
    }

    public void setConcepto(CajaConceptoBean concepto) {
        this.concepto = concepto;
    }
    
    public BigDecimal getImporteAsBigDecimal() {
        return FormatUtil.getInstance().desformateaBigDecimal(importe);
    }

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}    
    
}
