package com.comerzzia.bimbaylola.pos.services.core.procesadorIdFiscal;

import com.comerzzia.pos.services.ticket.Ticket;

@SuppressWarnings("rawtypes")
public interface IProcesadorFiscal {

    public String obtenerIdFiscal(Ticket ticket) throws ProcesadorIdFiscalException;
	
	public byte[] procesarDocumentoFiscal(Ticket ticket) throws Exception;

}
