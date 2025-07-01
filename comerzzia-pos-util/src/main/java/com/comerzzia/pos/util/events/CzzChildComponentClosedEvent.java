package com.comerzzia.pos.util.events;

import javafx.event.Event;
import javafx.event.EventType;

public class CzzChildComponentClosedEvent extends Event {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4032929664137705477L;
	
	public static EventType<CzzChildComponentClosedEvent> CLOSED_CHILD_EVENT = new EventType<CzzChildComponentClosedEvent>("CLOSED_CHILD_EVENT");

	public CzzChildComponentClosedEvent(EventType<? extends Event> eventType) {
		super(eventType);
	}

}
