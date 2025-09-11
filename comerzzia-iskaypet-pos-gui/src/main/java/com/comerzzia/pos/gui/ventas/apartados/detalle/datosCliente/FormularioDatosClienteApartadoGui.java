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
package com.comerzzia.pos.gui.ventas.apartados.detalle.datosCliente;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.validation.FormularioGui;

@Component
@Scope("prototype")
public class FormularioDatosClienteApartadoGui extends FormularioGui{

	@NotEmpty (message = "El campo descripción del cliente debe estar relleno.")
	@Size(max = 45, message = "La longitud del campo descripción no puede superar los {max} caracteres")
	protected String desCliente;
	
	@NotEmpty (message = "El campo domicilio del cliente debe estar relleno.")
	@Size(max = 50, message = "La longitud del campo domicilio no puede superar los {max} caracteres")
	protected String domicilio;
	
	@Size(max = 50, message = "La longitud del campo población no puede superar los {max} caracteres")
	protected String poblacion;
	
	@Size(max = 50, message = "La longitud del campo provincia no puede superar los {max} caracteres")
	protected String provincia;
	
	@Size(max = 50, message = "La longitud del campo localidad no puede superar los {max} caracteres")
	protected String localidad;
	
	@Size(max = 8, message = "La longitud del campo código postal no puede superar los {max} caracteres")
	protected String codigoPostal;
	
	@NotEmpty (message = "El campo num. documento del cliente debe estar relleno.")
	@Size(max = 20, message = "La longitud del campo  num. documento no puede superar los {max} caracteres")
	protected String cif;
	
	@Size(max = 15, message = "La longitud del campo teléfono no puede superar los {max} caracteres")
	protected String telefono;
	
	@NotEmpty (message = "El campo país del cliente debe estar relleno.")
	protected String codPais;
	
	public FormularioDatosClienteApartadoGui() {
		
	}
	
	public void setDesCliente(String desCliente) {
		this.desCliente = desCliente;
	}

	public void setDomicilio(String domicilio) {
		this.domicilio = domicilio;
	}
	
	public void setPoblacion(String poblacion) {
		this.poblacion = poblacion;
	}
	
	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}
	
	public void setLocalidad(String localidad) {
		this.localidad = localidad;
	}
	
	public void setCodigoPostal(String codigoPostal) {
		this.codigoPostal = codigoPostal;
	}
	
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public void setCif(String cif) {
		this.cif = cif;
	}

	public void setCodPais(String codPais) {
		this.codPais = codPais;
	}

	@Override
	public void limpiarFormulario() {
		desCliente   = "";
		domicilio    = "";
		poblacion    = "";
		provincia    = "";
		localidad    = "";
		codigoPostal = "";
		telefono     = "";
		cif     = "";
		codPais = "";
	}

}
