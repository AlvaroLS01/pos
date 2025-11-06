package com.comerzzia.dinosol.pos.services.ticket.cabecera.opciones;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;

public class OpcionPromocionesSeleccionadaDto {

	private String tituloOpcion;

	@XmlElementWrapper
	@XmlElement(name = "ahorro")
	private List<AhorroPromoDto> ahorrosPromociones;

	private String textoTicket;

	public String getTituloOpcion() {
		return tituloOpcion;
	}

	public void setTituloOpcion(String tituloOpcion) {
		this.tituloOpcion = tituloOpcion;
	}

	public List<AhorroPromoDto> getAhorrosPromociones() {
		return ahorrosPromociones;
	}

	public void addAhorroPromocion(Long idPromocion, BigDecimal ahorro, Long tipoDescuento) {
		if (this.ahorrosPromociones == null) {
			this.ahorrosPromociones = new ArrayList<AhorroPromoDto>();
		}
		AhorroPromoDto ahorroPromo = new AhorroPromoDto(idPromocion, BigDecimalUtil.redondear(ahorro), tipoDescuento);
		this.ahorrosPromociones.add(ahorroPromo);
	}

	public String getTextoTicket() {
		return textoTicket;
	}

	public void setTextoTicket(String textoTicket) {
		this.textoTicket = textoTicket;
	}

}
