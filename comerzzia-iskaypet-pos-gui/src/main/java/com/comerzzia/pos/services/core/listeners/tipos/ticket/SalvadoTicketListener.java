package com.comerzzia.pos.services.core.listeners.tipos.ticket;

import com.comerzzia.pos.services.core.listeners.POSListener;


public interface SalvadoTicketListener extends POSListener {
	
	public void executeBeforeSave(Object[] args) throws Exception;
	
	public void executeAfterSave(Object[] args) throws Exception;
	
	public void executeAfterCommit(Object[] args) throws Exception;

}
