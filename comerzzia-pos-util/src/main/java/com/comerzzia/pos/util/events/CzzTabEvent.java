package com.comerzzia.pos.util.events;

import javafx.event.Event;
import javafx.event.EventType;

public class CzzTabEvent extends Event {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7867613475359500762L;
	public static EventType<CzzTabEvent> TAB_EVENT = new EventType<CzzTabEvent>("TAB_EVENT");
	
	protected String message;

	public CzzTabEvent(EventType<? extends Event> arg0) {
		super(arg0);
	}

}
