package com.comerzzia.api.loyalty.persistence.customers.contacts;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class TiposContactoFidelizadoBean extends TiposContactoFidelizadoKey {

	private static final long serialVersionUID = 38353862291807371L;

	private String valor;

    private boolean recibeNotificaciones;
    
    private boolean recibeNotificacionesCom;

    private String desTipoCon;

    protected boolean puedeRecibirNotificaciones;
    
    //INICIO ATRIBUTOS PERSONALIZADOS--------------------------------------------
    public static final String EMAIL = "EMAIL";
    public static final String TELEFONO1 ="TELEFONO1";
    public static final String TELEFONO2 ="TELEFONO2";
    public static final String GOOGLE_GCM = "GOOGLE_GCM";
    public static final String FACEBOOK = "FACEBOOK";
    
    
    //FIN ATRIBUTOS PERSONALIZADOS-----------------------------------------------


    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor == null ? null : valor.trim();
    }

    public boolean getRecibeNotificaciones() {
        return recibeNotificaciones;
    }

    public void setRecibeNotificaciones(boolean recibeNotificaciones) {
        this.recibeNotificaciones = recibeNotificaciones;
    }
    
    public boolean isRecibeNotificacionesCom() {
        return recibeNotificacionesCom;
    }

    public void setRecibeNotificacionesCom(boolean recibeNotificacionesCom) {
        this.recibeNotificacionesCom = recibeNotificacionesCom;
    }

    public String getDesTipoCon() {
        return desTipoCon;
    }

    public void setDesTipoCon(String desTipoCon) {
        this.desTipoCon = desTipoCon == null ? null : desTipoCon.trim();
    }

    public boolean isPuedeRecibirNotificaciones() {
        return puedeRecibirNotificaciones;
    }

    public void setPuedeRecibirNotificaciones(boolean puedeRecibirNotificaciones) {
        this.puedeRecibirNotificaciones = puedeRecibirNotificaciones;
    }
    
    
    
    //INICIO MÉTODOS PERSONALIZADOS--------------------------------------------
    
    public void setRecibeNotificaciones(String recibeNotificaciones){
    	this.recibeNotificaciones = TRUE.equalsIgnoreCase(recibeNotificaciones) ? true:false;
    	
    }
    
    public void setRecibeNotificacionesCom(String recibeNotificacionesCom){
    	this.recibeNotificacionesCom = TRUE.equalsIgnoreCase(recibeNotificacionesCom) ? true:false;
    	
    }
    
    public void setPuedeRecibirNotificaciones(String puedeRecibirNotificaciones) {
    	this.puedeRecibirNotificaciones = TRUE.equalsIgnoreCase(puedeRecibirNotificaciones) ? true:false;
    }
    
    
    //FIN MÉTODOS PERSONALIZADOS-----------------------------------------------

}