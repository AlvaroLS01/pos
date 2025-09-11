package com.comerzzia.pos.services.core.listeners;


public interface POSListener {
	
	public int getProridad();
	
	public void execute(Object[] args) throws POSListenerException;
	
}
