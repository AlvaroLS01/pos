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

package com.comerzzia.pos.gui.ventas.tickets.factura;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.validation.FormularioGui;

@Component
@Scope("prototype")
public class FormularioDatosFacturaBean extends FormularioGui {

	@NotEmpty (message = "Debe rellenar el campo del nombre comercial del cliente.")
    @Size(max = 45, message = "La longitud del campo nombre comercial no puede superar los {max} caracteres")
    private String razonSocial;
	
    @Size(max = 50, message = "La longitud del campo domicilio no puede superar los {max} caracteres")
    @NotEmpty (message = "Debe rellenar el campo domicilio.")
    private String domicilio;
    
    @Size(max = 50, message = "La longitud del campo población no puede superar los {max} caracteres")
    private String poblacion;

    @Size(max = 50, message = "La longitud del campo provincia no puede superar los {max} caracteres")
    private String provincia;
    
    @Size(max = 50, message = "La longitud del campo localidad no puede superar los {max} caracteres")
    private String localidad;

    @Size(max = 8, message = "La longitud del campo código postal no puede superar los {max} caracteres")
    private String cPostal;
    
    @NotEmpty (message = "Debe rellenar el campo de identificación del cliente.")
    @Size(max = 20, message = "La longitud del campo número de documento no puede superar los {max} caracteres")
    private String numDocIdent;

    @Size(max = 15, message = "La longitud del campo teléfono no puede superar los {max} caracteres")
    private String telefono;
     
    @NotEmpty (message = "Debe seleccionar el país del cliente.")
    private String pais;
    
    @Size(max = 45, message = "La longitud del campo no puede superar los 45 caracteres.")
    private String banco;
    @Size(max = 50, message = "La longitud del campo no puede superar los 50 caracteres.")
    private String bancoDomicilio;
    @Size(max = 50, message = "La longitud del campo no puede superar los 50 caracteres.")
    private String bancoPoblacion;
    @Size(max = 20, message = "La longitud del campo no puede superar los 20 caracteres.")
    private String bancoCCC;

    public FormularioDatosFacturaBean() {

    }

    public FormularioDatosFacturaBean(String pais, String domicilio, String provincia, String numDocIdent, 
            String codPostal, String telefono, String poblacion, String razonSocial) {

        this.numDocIdent = numDocIdent;
        this.cPostal = codPostal;
        this.domicilio = domicilio;
        this.razonSocial = razonSocial;
        this.telefono = telefono;
        this.poblacion = poblacion;
        this.provincia = provincia;
        this.pais = pais;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getPoblacion() {
        return poblacion;
    }

    public void setPoblacion(String poblacion) {
        this.poblacion = poblacion;
    }
    
    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }
    
    public String getcPostal() {
        return cPostal;
    }

    public void setcPostal(String cPostal) {
        this.cPostal = cPostal;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getNumDocIdent() {
        return numDocIdent;
    }

    public void setNumDocIdent(String numDocIdent) {
        this.numDocIdent = numDocIdent;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getBanco() {
		return banco;
	}

	public void setBanco(String banco) {
		this.banco = banco;
	}

	public String getBancoDomicilio() {
		return bancoDomicilio;
	}

	public void setBancoDomicilio(String bancoDomicilio) {
		this.bancoDomicilio = bancoDomicilio;
	}

	public String getBancoPoblacion() {
		return bancoPoblacion;
	}

	public void setBancoPoblacion(String bancoPoblacion) {
		this.bancoPoblacion = bancoPoblacion;
	}

	public String getBancoCCC() {
		return bancoCCC;
	}

	public void setBancoCCC(String bancoCCC) {
		this.bancoCCC = bancoCCC;
	}

	@Override
    public void limpiarFormulario() {
    	razonSocial = "";
        domicilio = "";
        poblacion = "";
        provincia = "";
        localidad = "";
        cPostal = "";
        numDocIdent = "";
        telefono = "";
        pais = "";        
        
        banco = "";
        bancoDomicilio = "";
        bancoPoblacion = "";
        bancoCCC = "";
    }

}
