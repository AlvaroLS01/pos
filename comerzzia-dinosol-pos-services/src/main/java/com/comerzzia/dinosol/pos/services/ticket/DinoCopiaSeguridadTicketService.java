package com.comerzzia.dinosol.pos.services.ticket;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.dinosol.pos.services.cupones.CustomerCouponDTO;
import com.comerzzia.pos.persistence.tickets.aparcados.TicketAparcadoBean;
import com.comerzzia.pos.persistence.tickets.aparcados.TicketAparcadoMapper;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.ticket.TicketVenta;
import com.comerzzia.pos.services.ticket.copiaSeguridad.CopiaSeguridadTicketService;
import com.comerzzia.pos.services.ticket.profesional.TotalesTicketProfesional;
import com.comerzzia.pos.util.xml.MarshallUtil;
import com.comerzzia.pos.util.xml.MarshallUtilException;

@Component
@Primary
public class DinoCopiaSeguridadTicketService extends CopiaSeguridadTicketService {

	@Autowired
	protected Sesion sesion;

	@Autowired
	protected TicketAparcadoMapper ticketAparcadoMapper;
	
	@SuppressWarnings("rawtypes")
	public synchronized TicketAparcadoBean prepararTicketAparcado(TicketVenta ticket) throws MarshallUtilException, UnsupportedEncodingException {
		TicketAparcadoBean ta = new TicketAparcadoBean();
		// Seteamos los valores para el nuevo bean
		// Obtenemos el xml del ticket
		List<Class<?>> clasesAux = new ArrayList<Class<?>>();
		clasesAux.add(TotalesTicketProfesional.class);
		clasesAux.add(CustomerCouponDTO.class);
		byte[] xmlTicket = MarshallUtil.crearXML(ticket, clasesAux);
		log.trace("TICKET: " + ticket.getUidTicket() + "\n" + new String(xmlTicket, "UTF-8") + "\n");

		ta.setTicket(xmlTicket);
		// Atributos del ticket
		ta.setCodAlmacen(ticket.getTienda().getCodAlmacen());
		ta.setFecha(new Date());
		ta.setUidTicket(ticket.getUidTicket());
		ta.setUidActividad(sesion.getAplicacion().getUidActividad());

		// Atributos particulares de ticket aparcado
		ta.setUsuario(ticket.getCajero().getUsuario());
		ta.setCodCaja(ticket.getCodCaja());
		if (ticket.getCliente() != null) {
			ta.setCodCliente(ticket.getCliente().getCodCliente());
		}
		ta.setNumArticulos(ticket.getLineas().size());
		ta.setImporte(ticket.getTotales().getTotal());
		ta.setIdTipoDocumento(ticket.getCabecera().getTipoDocumento());
		return ta;
	}

}
