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
package com.comerzzia.pos.gui.ventas.cajas.apuntes.tablaApuntes;

import javafx.beans.property.SimpleStringProperty;

import com.comerzzia.pos.persistence.cajas.conceptos.CajaConceptoBean;

public class ConceptoGui {
	
	private SimpleStringProperty descripcion;
	
	private SimpleStringProperty codigo;
	
	private CajaConceptoBean concepto;
	
	public ConceptoGui(CajaConceptoBean concepto){
		descripcion = new SimpleStringProperty();
		codigo = new SimpleStringProperty();
		descripcion.setValue(concepto.getDesConceptoMovimiento());
		codigo.setValue(concepto.getCodConceptoMovimiento());
		this.concepto = concepto;
	}

	public String getDescripcion() {
		return descripcion.getValue();
	}

	public void setDescripcion(String descripcion) {
		this.descripcion.setValue(descripcion);
	}

	public String getCodigo() {
		return codigo.getValue();
	}

	public void setCodigo(String codigo) {
		this.codigo.setValue(codigo);
	}

	public SimpleStringProperty getDescripcionProperty(){
		return descripcion;
	}
	
	public SimpleStringProperty getCodigoProperty(){
		return codigo;
	}
	
	public CajaConceptoBean getConcepto(){
		return concepto;
	}
}
