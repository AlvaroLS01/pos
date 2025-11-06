package com.comerzzia.dinosol.pos.services.ticket.lineas;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.dinosol.pos.services.articulos.propiedades.PropiedadArticuloService;
import com.comerzzia.dinosol.pos.services.ticket.cabecera.DinoCabeceraTicket;
import com.comerzzia.pos.services.articulos.ArticuloNotFoundException;
import com.comerzzia.pos.services.ticket.TicketVenta;
import com.comerzzia.pos.services.ticket.lineas.LineaTicketAbstract;
import com.comerzzia.pos.services.ticket.lineas.LineaTicketException;
import com.comerzzia.pos.services.ticket.lineas.LineasTicketServices;

@Component
@Primary
public class DinoLineasTicketServices extends LineasTicketServices {

	@Autowired
	protected PropiedadArticuloService propiedadArticuloService;
	
	@Override
	public LineaTicketAbstract createLineaArticulo(@SuppressWarnings("rawtypes") TicketVenta ticket, String codigo, String desglose1, String desglose2, BigDecimal cantidad, BigDecimal precio, LineaTicketAbstract lineaTicket) throws LineaTicketException, ArticuloNotFoundException {
		LineaTicketAbstract linea = super.createLineaArticulo(ticket, codigo, desglose1, desglose2, cantidad, precio, lineaTicket);
		
		tratarArticuloPlastico(linea);
		
		tratarLineasPosReparto(ticket, linea);
		
		addDatosAnexos(linea);
		
		return linea;
	}

	private void addDatosAnexos(LineaTicketAbstract linea) {
		((DinoLineaTicket) linea).setHoraRegistro(new Date());
	}

	@SuppressWarnings("rawtypes")
	protected void tratarLineasPosReparto(TicketVenta ticket, LineaTicketAbstract linea) {
		if (((DinoCabeceraTicket) ticket.getCabecera()).getServicioRepartoDto() != null) {
			((DinoLineaTicket) linea).setAdmitePromociones(false);
		}
	}

	protected void tratarArticuloPlastico(LineaTicketAbstract linea) {
		String codArtPlastico = propiedadArticuloService.consultarCodartPlastico(linea.getCodArticulo());
		boolean esPlastico = propiedadArticuloService.consultarEsPlastico(linea.getCodArticulo());
		
		if(StringUtils.isNotBlank(codArtPlastico)) {
			((DinoLineaTicket) linea).setCodArtPlasticoAsociado(codArtPlastico);
		}
		if(esPlastico) {
			((DinoLineaTicket) linea).setEsPlastico(esPlastico);
		}
	}
	
}
