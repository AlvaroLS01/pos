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
package com.comerzzia.pos.gui.ventas.tickets.articulos.facturarVentaCredito;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.validation.FormularioGui;

@Component
@Scope("prototype")
public class FormularioDatosVentaCreditoBean extends FormularioGui{

	@NotEmpty (message = "Debe introducir el código de caja.")
	String codCaja;
	
	@NotEmpty (message = "Debe introducir el código de tienda.")
	String codTienda;
	
	@NotEmpty (message = "Debe introducir el código de identificación del documento.")
	String idDoc;
	
	@NotEmpty (message = "Debe introducir el código de identificación del documento.")
	String tipoDoc;
	
	public FormularioDatosVentaCreditoBean(){
		
	}
	
	public FormularioDatosVentaCreditoBean(String codCaja, String codTienda, String idDoc, String tipoDoc){
		
		this.codCaja = codCaja;
		this.codTienda = codTienda;
		this.idDoc = idDoc;
		this.tipoDoc = tipoDoc;
	}
	
	public String getCodCaja() {
		return codCaja;
	}

	public void setCodCaja(String codCaja) {
		this.codCaja = codCaja;
	}

	public String getCodTienda() {
		return codTienda;
	}

	public void setCodTienda(String codTienda) {
		this.codTienda = codTienda;
	}

	public String getIdDoc() {
		return idDoc;
	}

	public void setIdDoc(String idDoc) {
		this.idDoc = idDoc;
	}

	public String getTipoDoc() {
		return tipoDoc;
	}

	public void setTipoDoc(String tipoDoc) {
		this.tipoDoc = tipoDoc;
	}

	@Override
	public void limpiarFormulario() {
		codCaja = "";
		codTienda = "";
		idDoc = "";
		tipoDoc = "";
	}

}
