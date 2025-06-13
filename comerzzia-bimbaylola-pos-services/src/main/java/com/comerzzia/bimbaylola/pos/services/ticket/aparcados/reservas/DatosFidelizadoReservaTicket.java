package com.comerzzia.bimbaylola.pos.services.ticket.aparcados.reservas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@XmlAccessorType(XmlAccessType.FIELD)
public class DatosFidelizadoReservaTicket{

	@XmlElement(name = "idFidelizado")
	private String idFidelizado;
	@XmlElement(name = "numero_tarjeta")
	private String numeroTarjeta;
	@XmlElement(name = "nombre")
	private String nombre;
	@XmlElement(name = "apellidos")
	private String apellidos;
	
    public String getIdFidelizado(){
    	return idFidelizado;
    }
	
    public void setIdFidelizado(String idFidelizado){
    	this.idFidelizado = idFidelizado;
    }
	
    public String getNumeroTarjeta(){
    	return numeroTarjeta;
    }
	
    public void setNumeroTarjeta(String numeroTarjeta){
    	this.numeroTarjeta = numeroTarjeta;
    }
	
    public String getNombre(){
    	return nombre;
    }
	
    public void setNombre(String nombre){
    	this.nombre = nombre;
    }
	
    public String getApellidos(){
    	return apellidos;
    }
	
    public void setApellidos(String apellidos){
    	this.apellidos = apellidos;
    }
	
}
