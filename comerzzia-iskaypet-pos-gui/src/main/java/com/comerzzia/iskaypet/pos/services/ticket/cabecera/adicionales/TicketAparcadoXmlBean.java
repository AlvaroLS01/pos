package com.comerzzia.iskaypet.pos.services.ticket.cabecera.adicionales;

import com.comerzzia.pos.util.format.FormatUtil;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.math.BigDecimal;
import java.util.Date;

/**
 * CZZ-115 - APARCAR TICKET
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class TicketAparcadoXmlBean {

	@XmlElement(name = "caja")
	protected String caja;
	@XmlElement(name = "usuario")
	protected String usuario;
	@XmlElement(name = "fecha")
	protected String fecha;

	public TicketAparcadoXmlBean(){

	}

	public TicketAparcadoXmlBean(String caja, String usuario) {
		this.caja = caja;
		this.usuario = usuario;
	}

	public String getCaja() {
		return caja;
	}

	public void setCaja(String caja) {
		this.caja = caja;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public Date getFecha() {
		if (fecha == null) {
			return null;
		}
		return FormatUtil.getInstance().desformateaFechaHoraTicket(fecha);
	}

	public String getFechaAsLocale() {

		Date fecha = getFecha();
		if (fecha == null) {
			fecha = new Date();
		}
		String fechaTicket = FormatUtil.getInstance().formateaFechaCorta(fecha);
		String horaTicket = FormatUtil.getInstance().formateaHora(fecha);
		return fechaTicket + " " + horaTicket;
	}

	public void setFecha(Date fecha) {
		this.fecha = FormatUtil.getInstance().formateaFechaHoraTicket(fecha);
	}
}
