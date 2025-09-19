package com.comerzzia.bimbaylola.pos.services.ticket.aparcados.reservas;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Objeto para contener los datos para la creación de un Ticket de Reserva.
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "ticket")
@XmlAccessorType(XmlAccessType.FIELD)
public class TicketReserva{

	@XmlElement(name = "cabecera")
	private CabeceraReservaTicket cabecera;
	@XmlElementWrapper(name = "lineas")
	@XmlElement(name = "linea")
	private List<LineaReservaTicket> lineas;
	@XmlElementWrapper(name = "pagos")
	@XmlElement(name = "pago")
	private List<PagoReservaTicket> pagos;
	@XmlTransient
	private byte[] xmlTicket;
	
	public CabeceraReservaTicket getCabecera(){
		return cabecera;
	}
	public void setCabecera(CabeceraReservaTicket cabecera){
		this.cabecera = cabecera;
	}
	public List<LineaReservaTicket> getLineas(){
		return lineas;
	}
	public void setLineas(List<LineaReservaTicket> lineas){
		this.lineas = lineas;
	}
	public List<PagoReservaTicket> getPagos(){
		return pagos;
	}
	public void setPagos(List<PagoReservaTicket> pagos){
		this.pagos = pagos;
	}
	public byte[] getXmlTicket(){
		return xmlTicket;
	}
	public void setXmlTicket(byte[] xmlTicket){
		this.xmlTicket = xmlTicket;
	}
	
}
