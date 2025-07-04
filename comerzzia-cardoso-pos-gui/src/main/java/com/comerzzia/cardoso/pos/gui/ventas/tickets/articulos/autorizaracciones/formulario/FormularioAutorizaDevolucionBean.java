package com.comerzzia.cardoso.pos.gui.ventas.tickets.articulos.autorizaracciones.formulario;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.validation.FormularioGui;

/**
 * GAP - CAJERO AUXILIAR
 */
@Component
@Scope("prototype")
public class FormularioAutorizaDevolucionBean extends FormularioGui{

	@NotEmpty(message = "Campo requerido")
	private String usuario;
	@NotEmpty(message = "Campo requerido")
	private String pass;
	@NotEmpty(message = "Campo requerido")
	private String documento;
	@NotEmpty(message = "Campo requerido")
	private String tienda;

	@Override
	public void limpiarFormulario(){
		this.usuario = "";
		this.pass = "";
		this.documento = "";
		this.tienda = "";
	}

	public FormularioAutorizaDevolucionBean(){

	}

	public FormularioAutorizaDevolucionBean(String usuario, String pass, String documento, String tienda){
		this.usuario = usuario;
		this.pass = pass;
		this.documento = documento;
		this.tienda = tienda;
	}

	public String getUsuario(){
		return usuario;
	}

	public void setUsuario(String usuario){
		this.usuario = usuario;
	}

	public String getPass(){
		return pass;
	}

	public void setPass(String pass){
		this.pass = pass;
	}

	public String getDocumento(){
		return documento;
	}

	public void setDocumento(String documento){
		this.documento = documento;
	}

	public String getTienda(){
		return tienda;
	}

	public void setTienda(String tienda){
		this.tienda = tienda;
	}
	
}
