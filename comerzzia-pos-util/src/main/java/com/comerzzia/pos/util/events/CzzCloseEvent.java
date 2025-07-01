package com.comerzzia.pos.util.events;

import javafx.event.Event;
import javafx.event.EventType;

public class CzzCloseEvent extends Event{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6739023056251557360L;
	
	public static EventType<CzzCloseEvent> CLOSE_EVENT = new EventType<CzzCloseEvent>("CLOSE_EVENT");
	public static EventType<CzzCloseEvent> CLOSE_SCENE_EVENT = new EventType<CzzCloseEvent>("CLOSE_SCENE_EVENT");

	public CzzCloseEvent(EventType<? extends Event> arg0) {
		super(arg0);
	}

}
