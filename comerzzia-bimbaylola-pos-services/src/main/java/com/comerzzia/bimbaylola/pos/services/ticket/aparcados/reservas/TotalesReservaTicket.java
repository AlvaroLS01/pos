package com.comerzzia.bimbaylola.pos.services.ticket.aparcados.reservas;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Objeto para contener el total de un Ticket de Reserva.
 */
@Component
@Scope("prototype")
@XmlAccessorType(XmlAccessType.FIELD)
public class TotalesReservaTicket{

	@XmlElement(name = "total")
	private BigDecimal total;

	public BigDecimal getTotal(){
		return total;
	}
	public void setTotal(BigDecimal total){
		this.total = total;
	}
	
}
