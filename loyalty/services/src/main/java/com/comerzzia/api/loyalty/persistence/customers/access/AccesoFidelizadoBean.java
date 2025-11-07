package com.comerzzia.api.loyalty.persistence.customers.access;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonIgnore;

@XmlRootElement(name="accesoFidelizado")
@XmlType(name="accFid")
public class AccesoFidelizadoBean extends AccesoFidelizadoKey {

	
	private static final long serialVersionUID = 5437429895255113961L;

	private Long idFidelizado;

    private String email;

    private String clave;

    @XmlTransient
    private Date fechaAlta;

    @XmlTransient
    private Date fechaModificacion;

    @XmlTransient
    private Date fechaModificacionClave;

    @XmlTransient
    private Date fechaBaja;

    @XmlTransient
    private String claveReseteada;

    private String emailVerificado;

    @XmlTransient
    private Date fechaUltimoLogin;

    @XmlTransient
    private Date fechaUltimoIntentoFallido;

    @XmlTransient
    private Short numeroIntentosFallidos;

    private String recuerdoClavePregunta;

    private String recuerdoClaveRespuesta;
    
    //INICIO ATRIBUTOS PERSONALIZADOS--------------------------------------------
    
    private String nuevaClave;
    
    //FIN ATRIBUTOS PERSONALIZADOS-----------------------------------------------


    public Long getIdFidelizado() {
        return idFidelizado;
    }

    public void setIdFidelizado(Long idFidelizado) {
        this.idFidelizado = idFidelizado;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave == null ? null : clave.trim();
    }

    @JsonIgnore
    public Date getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(Date fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    @JsonIgnore
    public Date getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(Date fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    @JsonIgnore
    public Date getFechaModificacionClave() {
        return fechaModificacionClave;
    }

    public void setFechaModificacionClave(Date fechaModificacionClave) {
        this.fechaModificacionClave = fechaModificacionClave;
    }

    @JsonIgnore
    public Date getFechaBaja() {
        return fechaBaja;
    }

    public void setFechaBaja(Date fechaBaja) {
        this.fechaBaja = fechaBaja;
    }

    @JsonIgnore
    public String getClaveReseteada() {
        return claveReseteada;
    }

    public void setClaveReseteada(String claveReseteada) {
        this.claveReseteada = claveReseteada == null ? null : claveReseteada.trim();
    }

    public String getEmailVerificado() {
        return emailVerificado;
    }

    public void setEmailVerificado(String emailVerificado) {
    	if(emailVerificado != null){
			if(emailVerificado.equalsIgnoreCase(TRUE_BOOLEAN)){
				this.emailVerificado = TRUE;
			}else if(emailVerificado.equalsIgnoreCase(FALSE_BOOLEAN)){
				this.emailVerificado = FALSE;
			}
		}else{
			this.emailVerificado = FALSE;
		}
    }

    @JsonIgnore
    public Date getFechaUltimoLogin() {
        return fechaUltimoLogin;
    }

    public void setFechaUltimoLogin(Date fechaUltimoLogin) {
        this.fechaUltimoLogin = fechaUltimoLogin;
    }

    @JsonIgnore
    public Date getFechaUltimoIntentoFallido() {
        return fechaUltimoIntentoFallido;
    }

    public void setFechaUltimoIntentoFallido(Date fechaUltimoIntentoFallido) {
        this.fechaUltimoIntentoFallido = fechaUltimoIntentoFallido;
    }

    @JsonIgnore
    public Short getNumeroIntentosFallidos() {
        return numeroIntentosFallidos;
    }

    public void setNumeroIntentosFallidos(Short numeroIntentosFallidos) {
        this.numeroIntentosFallidos = numeroIntentosFallidos;
    }

    public String getRecuerdoClavePregunta() {
        return recuerdoClavePregunta;
    }

    public void setRecuerdoClavePregunta(String recuerdoClavePregunta) {
        this.recuerdoClavePregunta = recuerdoClavePregunta == null ? null : recuerdoClavePregunta.trim();
    }

    public String getRecuerdoClaveRespuesta() {
        return recuerdoClaveRespuesta;
    }

    public void setRecuerdoClaveRespuesta(String recuerdoClaveRespuesta) {
        this.recuerdoClaveRespuesta = recuerdoClaveRespuesta == null ? null : recuerdoClaveRespuesta.trim();
    }

    //INICIO MÉTODOS PERSONALIZADOS--------------------------------------------
    
    public String getNuevaClave() {
    	return nuevaClave;
    }
    
    public void setNuevaClave(String nuevaClave) {
    	this.nuevaClave = nuevaClave;
    }
    
    //FIN MÉTODOS PERSONALIZADOS-----------------------------------------------

}