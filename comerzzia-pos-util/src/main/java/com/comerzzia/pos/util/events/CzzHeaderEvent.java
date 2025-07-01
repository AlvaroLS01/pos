package com.comerzzia.pos.util.events;

import javafx.event.Event;
import javafx.event.EventType;

public class CzzHeaderEvent extends Event {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5353111034077619193L;
	public static EventType<CzzHeaderEvent> OPEN_MENU_EVENT = new EventType<CzzHeaderEvent>("OPEN_MENU_EVENT");
	
	public CzzHeaderEvent(EventType<? extends Event> arg0) {
		super(arg0);
	}
}
