package com.comerzzia.bimbaylola.pos.services.ticket.aparcados.reservas;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Objeto para contener todos los datos referentes a la cabecera de un Ticket Reserva que no suelen
 * aparecer en una cabecera de Ticket común.
 */
@Component
@Scope("prototype")
@XmlAccessorType(XmlAccessType.FIELD)
public class CabeceraEspecialesReservaTicket{

	@XmlElement(name = "usuario")
	private String usuario;
	@XmlElement(name = "saldo_cliente")
	private BigDecimal saldoCliente;
	@XmlElement(name = "referencia_cliente")
	private String referenciaCliente;
	@XmlElement(name = "observaciones")
	private String observaciones;
	@XmlElement(name = "version")
	private Long version;
	
	public String getUsuario(){
		return usuario;
	}
	public void setUsuario(String usuario){
		this.usuario = usuario;
	}
	public BigDecimal getSaldoCliente(){
		return saldoCliente;
	}
	public void setSaldoCliente(BigDecimal saldoCliente){
		this.saldoCliente = saldoCliente;
	}
	public String getReferenciaCliente(){
		return referenciaCliente;
	}
	public void setReferenciaCliente(String referenciaCliente){
		this.referenciaCliente = referenciaCliente;
	}
	public String getObservaciones(){
		return observaciones;
	}
	public void setObservaciones(String observaciones){
		this.observaciones = observaciones;
	}
	public Long getVersion(){
		return version;
	}
	public void setVersion(Long version){
		this.version = version;
	}
	
}
