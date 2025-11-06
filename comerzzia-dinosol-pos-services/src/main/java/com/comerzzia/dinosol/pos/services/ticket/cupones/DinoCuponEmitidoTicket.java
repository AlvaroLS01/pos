package com.comerzzia.dinosol.pos.services.ticket.cupones;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.comerzzia.pos.services.ticket.cupones.CuponEmitidoTicket;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "cupon")
public class DinoCuponEmitidoTicket extends CuponEmitidoTicket {

	protected Date fechaFin;

	public Date getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

}
