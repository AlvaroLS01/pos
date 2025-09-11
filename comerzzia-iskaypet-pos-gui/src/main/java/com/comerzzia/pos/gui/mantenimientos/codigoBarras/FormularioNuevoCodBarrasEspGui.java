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
package com.comerzzia.pos.gui.mantenimientos.codigoBarras;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.validation.FormularioGui;
import com.comerzzia.pos.core.gui.validation.validators.number.esnumerico.EsNumerico;

@Component
@Scope("prototype")
public class FormularioNuevoCodBarrasEspGui extends FormularioGui{

	@NotNull (message = "Debe rellenar el campo descripción.")
	@NotEmpty (message = "Debe rellenar el campo descripción.")
	String descripcion;
	@NotNull (message = "Debe rellenar el campo longitud del contenido.")
	@NotEmpty (message = "Debe rellenar el campo longitud del contenido.")
	@Size(max = 2, message = "La longitud del campo no puede superar los 2 caracteres.")
	@EsNumerico(message = "El valor introducido debe ser numérico.")
	String longitudContenido;
	@NotNull (message = "Debe rellenar el campo posición del contenido.")
	@NotEmpty (message = "Debe rellenar el campo posición del contenido.")
	@Size(max = 2, message = "La longitud del campo no puede superar los 2 caracteres.")
	@EsNumerico(message = "El valor introducido debe ser numérico.")
	String posicionContenido;
	@NotNull (message = "Debe rellenar el campo posición de la cantidad.")
	@NotEmpty (message = "Debe rellenar el campo posición de la cantidad.")
	@Size(max = 2, message = "La longitud del campo no puede superar los 2 caracteres.")
	@EsNumerico(message = "El valor introducido debe ser numérico.")
	String posicionCantidad;
	@NotNull (message = "Debe rellenar el campo posición del precio.")
	@NotEmpty (message = "Debe rellenar el campo posición del precio.")
	@Size(max = 2, message = "La longitud del campo no puede superar los 2 caracteres.")
	@EsNumerico(message = "El valor introducido debe ser numérico.")
	String posicionPrecio;
	@NotNull (message = "Debe rellenar el campo parte decimal de la cantidad.")
	@NotEmpty (message = "Debe rellenar el campo parte decimal de la cantidad.")
	@Size(max = 2, message = "La longitud del campo no puede superar los 2 caracteres.")
	@EsNumerico(message = "El valor introducido debe ser numérico.")
	String decimalesCantidad;
	@NotNull (message = "Debe rellenar el campo parte decimal del precio.")
	@NotEmpty (message = "Debe rellenar el campo parte decimal del precio.")
	@Size(max = 2, message = "La longitud del campo no puede superar los 2 caracteres.")
	@EsNumerico(message = "El valor introducido debe ser numérico.")
	String decimalesPrecio;
	@NotNull (message = "Debe rellenar el campo tipo de código.")
	@NotEmpty (message = "Debe rellenar el campo tipo de código.")	
	String tipoCodigo;
	@NotNull (message = "Debe rellenar el campo parte entera de la cantidad.")
	@NotEmpty (message = "Debe rellenar el campo parte entera de la cantidad.")
	@Size(max = 2, message = "La longitud del campo no puede superar los 2 caracteres.")
	@EsNumerico(message = "El valor introducido debe ser numérico.")
	String enterosCantidad;
	@NotNull (message = "Debe rellenar el campo parte entera del precio.")
	@NotEmpty (message = "Debe rellenar el campo parte entera del precio.")
	@Size(max = 2, message = "La longitud del campo no puede superar los 2 caracteres.")
	@EsNumerico(message = "El valor introducido debe ser numérico.")
	String enterosPrecio;
	@NotNull (message = "Debe rellenar el campo prefijo.")
	@NotEmpty (message = "Debe rellenar el campo prefijo.")
	@EsNumerico(message = "El valor introducido debe ser numérico.")
	String prefijo;
		
	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getLongitudContenido() {
		return longitudContenido;
	}

	public void setLongitudContenido(String longitudContenido) {
		this.longitudContenido = longitudContenido;
	}

	public String getPosicionContenido() {
		return posicionContenido;
	}

	public void setPosicionContenido(String posicionContenido) {
		this.posicionContenido = posicionContenido;
	}

	public String getPosicionCantidad() {
		return posicionCantidad;
	}

	public void setPosicionCantidad(String posicionCantidad) {
		this.posicionCantidad = posicionCantidad;
	}

	public String getPosicionPrecio() {
		return posicionPrecio;
	}

	public void setPosicionPrecio(String posicionPrecio) {
		this.posicionPrecio = posicionPrecio;
	}

	public String getDecimalesCantidad() {
		return decimalesCantidad;
	}

	public void setDecimalesCantidad(String decimalesCantidad) {
		this.decimalesCantidad = decimalesCantidad;
	}

	public String getDecimalesPrecio() {
		return decimalesPrecio;
	}

	public void setDecimalesPrecio(String decimalesPrecio) {
		this.decimalesPrecio = decimalesPrecio;
	}

	public String getTipoCodigo() {
		return tipoCodigo;
	}

	public void setTipoCodigo(String tipoCodigo) {
		this.tipoCodigo = tipoCodigo;
	}

	public String getEnterosCantidad() {
		return enterosCantidad;
	}

	public void setEnterosCantidad(String enterosCantidad) {
		this.enterosCantidad = enterosCantidad;
	}

	public String getEnterosPrecio() {
		return enterosPrecio;
	}

	public void setEnterosPrecio(String enterosPrecio) {
		this.enterosPrecio = enterosPrecio;
	}
	
	public void setPrefijo(String prefijo){
		this.prefijo = prefijo;
	}
	
	public String getPrefijo(){
		return prefijo;
	}

	@Override
	public void limpiarFormulario() {
		
		descripcion = "";
		enterosCantidad = "";
		enterosPrecio = "";
		tipoCodigo = "";
		decimalesPrecio = "";
		decimalesCantidad = "";
		longitudContenido = "";
		posicionCantidad = "";
		posicionContenido = "";
		posicionPrecio = "";
	}

}
