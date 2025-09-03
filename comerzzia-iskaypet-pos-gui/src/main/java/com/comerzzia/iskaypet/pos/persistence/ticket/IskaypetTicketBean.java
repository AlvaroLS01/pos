package com.comerzzia.iskaypet.pos.persistence.ticket;

import org.springframework.context.annotation.Primary;

import com.comerzzia.pos.persistence.tickets.TicketBean;

@Primary
public class IskaypetTicketBean extends TicketBean {
	
	private String contrato;

	public String getContrato() {
		return contrato;
	}

	public void setContrato(String contrato) {
		this.contrato = contrato;
	}
	
}
