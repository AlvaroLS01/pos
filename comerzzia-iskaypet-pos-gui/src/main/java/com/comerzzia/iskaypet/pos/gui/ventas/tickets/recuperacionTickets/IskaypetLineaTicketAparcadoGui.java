package com.comerzzia.iskaypet.pos.gui.ventas.tickets.recuperacionTickets;

import com.comerzzia.pos.gui.ventas.tickets.recuperacionTickets.LineaTicketAparcadoGui;
import com.comerzzia.pos.persistence.tickets.aparcados.TicketAparcadoBean;

public class IskaypetLineaTicketAparcadoGui extends LineaTicketAparcadoGui {

	private String contrato;
	public IskaypetLineaTicketAparcadoGui(TicketAparcadoBean ticket) {
		super(ticket);
	}
	public String getContrato() {
		return contrato;
	}
	public void setContrato(String contrato) {
		this.contrato = contrato;
	}
	
	

}
