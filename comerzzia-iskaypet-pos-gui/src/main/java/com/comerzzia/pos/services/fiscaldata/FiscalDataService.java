package com.comerzzia.pos.services.fiscaldata;

import com.comerzzia.pos.services.ticket.ITicket;

public interface FiscalDataService {

	FiscalData getFiscalData(ITicket ticketPrincipal);
	
}
