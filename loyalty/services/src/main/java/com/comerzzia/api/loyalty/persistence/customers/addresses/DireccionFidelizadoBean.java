package com.comerzzia.api.loyalty.persistence.customers.addresses;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.comerzzia.core.model.direcciones.Direccion;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class DireccionFidelizadoBean extends DireccionFidelizadoKey implements Direccion {
	
	private static final long serialVersionUID = 3667597259126839538L;

	private String domicilio;

    private String poblacion;

    private String localidad;

    private String provincia;

    private String cp;

    private String codPais;

    private String desPais;
    
    //INICIO ATRIBUTOS PERSONALIZADOS--------------------------------------------
    
    private boolean principal = false;
    
    //FIN ATRIBUTOS PERSONALIZADOS-----------------------------------------------


    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio == null ? null : domicilio.trim();
    }

    public String getPoblacion() {
        return poblacion;
    }

    public void setPoblacion(String poblacion) {
        this.poblacion = poblacion == null ? null : poblacion.trim();
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad == null ? null : localidad.trim();
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia == null ? null : provincia.trim();
    }

    public String getCp() {
        return cp;
    }

    public void setCp(String cp) {
        this.cp = cp == null ? null : cp.trim();
    }

    public String getCodPais() {
        return codPais;
    }

    public void setCodPais(String codPais) {
        this.codPais = codPais == null ? null : codPais.trim();
    }

    public String getDesPais() {
        return desPais;
    }

    public void setDesPais(String desPais) {
        this.desPais = desPais == null ? null : desPais.trim();
    }
    
    //INICIO MÉTODOS PERSONALIZADOS--------------------------------------------
    
    public boolean isPrincipal() {
    	return principal;
    }
    
    public void setPrincipal(boolean principal) {
    	this.principal = principal;
    }
    
    //FIN MÉTODOS PERSONALIZADOS-----------------------------------------------

}