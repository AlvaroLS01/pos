package com.comerzzia.pos.util.listeners;

public interface POSListener {

	public int getPriority();

	public void execute(Object[] args) throws POSListenerException;

}
