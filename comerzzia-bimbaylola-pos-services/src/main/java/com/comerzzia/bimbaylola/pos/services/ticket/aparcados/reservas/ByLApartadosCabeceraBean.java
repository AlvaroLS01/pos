package com.comerzzia.bimbaylola.pos.services.ticket.aparcados.reservas;

import com.comerzzia.pos.persistence.apartados.ApartadosCabeceraBean;

import javax.xml.bind.annotation.XmlElement;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class ByLApartadosCabeceraBean extends ApartadosCabeceraBean {

	private DatosFidelizadoReservaTicket datosFidelizado;

	private String codCaja;

	@XmlElement(name = "esPagoAnticipo")
	protected String esPagoAnticipo;

	public String getCodCaja() {
		return codCaja;
	}

	public void setCodCaja(String codCaja) {
		this.codCaja = codCaja;
	}

	public DatosFidelizadoReservaTicket getDatosFidelizado() {
		return datosFidelizado;
	}

	public void setDatosFidelizado(DatosFidelizadoReservaTicket datosFidelizado) {
		this.datosFidelizado = datosFidelizado;
	}

	public String getEsPagoAnticipo() {
		return esPagoAnticipo;
	}

	public void setEsPagoAnticipo(String esPagoAnticipo) {
		this.esPagoAnticipo = esPagoAnticipo;
	}
}
