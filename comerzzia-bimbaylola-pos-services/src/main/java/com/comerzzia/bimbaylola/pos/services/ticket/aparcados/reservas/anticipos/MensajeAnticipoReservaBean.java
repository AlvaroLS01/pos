package com.comerzzia.bimbaylola.pos.services.ticket.aparcados.reservas.anticipos;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.comerzzia.bimbaylola.pos.services.ticket.aparcados.reservas.PagoReservaTicket;

@XmlRootElement(name = "anticipoReserva")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "cabecera", "pagos" })
public class MensajeAnticipoReservaBean {

	@XmlElement(name = "cabecera")
	protected MensajeAnticipoReservaCabeceraBean cabecera;

	@XmlElementWrapper(name = "pagos")
	@XmlElement(name = "pago")
	protected List<PagoReservaTicket> pagos;

	public MensajeAnticipoReservaBean() {
		pagos = new ArrayList<PagoReservaTicket>();
	}

	public MensajeAnticipoReservaBean(MensajeAnticipoReservaCabeceraBean cabecera, List<PagoReservaTicket> pagos) {
		super();
		this.cabecera = cabecera;
		this.pagos = pagos;
	}

	public MensajeAnticipoReservaCabeceraBean getCabecera() {
		return cabecera;
	}

	public void setCabecera(MensajeAnticipoReservaCabeceraBean cabecera) {
		this.cabecera = cabecera;
	}

	public List<PagoReservaTicket> getPagos() {
		return pagos;
	}

	public void setPagos(List<PagoReservaTicket> pagos) {
		this.pagos = pagos;
	}

	@Override
	public String toString() {
		return "MensajeAnticipoReservaBean [cabecera=" + cabecera + ", pagos=" + pagos + "]";
	}

}
