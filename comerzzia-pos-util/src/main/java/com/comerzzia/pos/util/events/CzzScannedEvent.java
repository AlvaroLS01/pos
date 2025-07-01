package com.comerzzia.pos.util.events;

import javafx.event.Event;
import javafx.event.EventType;

public class CzzScannedEvent extends Event{
	private static final long serialVersionUID = -7832300853730309102L;

	public static final EventType<CzzScannedEvent> SCANNED_EVENT = new EventType<>("SCANNED_EVENT");

	private final Object eventData;
	
	public CzzScannedEvent(EventType<? extends Event> eventType, Object eventData) {
		super(eventType);
		this.eventData = eventData;
	}
	
	public Object getEventData() {
		return eventData;
	}

}
