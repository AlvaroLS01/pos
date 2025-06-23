package com.comerzzia.bimbaylola.pos.devices.impresoras.fiscal;

import com.comerzzia.pos.services.ticket.TicketVentaAbono;

public interface IFiscalPrinter {
	
	public void sendTicket(TicketVentaAbono ticket) throws Throwable;
	
	public void imprimirTicketRegalo(TicketVentaAbono ticket) throws Throwable;

}
