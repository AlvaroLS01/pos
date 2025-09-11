package com.comerzzia.pos.gui.ventas.tickets.pagos.datosEnvio;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.validation.FormularioGui;

@Component
@Scope("prototype")
public class FormularioDatosEnvio extends FormularioGui {

	@NotEmpty(message = "Debe rellenar el campo nombre.")
	@Size(max = 45, message = "La longitud del campo nombre no puede superar los {max} caracteres") 
	private String nombre;

	@NotEmpty(message = "Debe rellenar el campo domicilio.")
	@Size(max = 50, message = "La longitud del campo domicilio no puede superar los {max} caracteres")
	private String domicilio;
	
	@Size(max = 50, message = "La longitud del campo población no puede superar los {max} caracteres")
	protected String poblacion;
	
	@Size(max = 50, message = "La longitud del campo provincia no puede superar los {max} caracteres")
	protected String provincia;
	
	@Size(max = 50, message = "La longitud del campo localidad no puede superar los {max} caracteres")
	protected String localidad;
	
	@Size(max = 8, message = "La longitud del campo código postal no puede superar los {max} caracteres")
	protected String codigoPostal;
	
	//@NotEmpty (message = "El campo num. documento del cliente debe estar relleno.")
	@Size(max = 20, message = "La longitud del campo num. documento no puede superar los {max} caracteres")
	protected String numDocIdent;

	@NotEmpty(message = "Debe rellenar el campo telefono.")
	@Size(max = 15, message = "La longitud del campo teléfono no puede superar los {max} caracteres")
	private String telefono;
	

	public void limpiarFormulario() {
		nombre = "";
		domicilio = "";
		telefono = "";
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDomicilio() {
		return domicilio;
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

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	
	public void setnumDocIdent(String numDocIdent) {
		this.numDocIdent = numDocIdent;
	}
}
