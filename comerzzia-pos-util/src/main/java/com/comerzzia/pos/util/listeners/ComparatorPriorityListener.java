package com.comerzzia.pos.util.listeners;

import java.util.Comparator;

public class ComparatorPriorityListener implements Comparator<POSListener> {

	@Override
	public int compare(POSListener o1, POSListener o2) {
		Integer listener1Priority = o1.getPriority();
		Integer listener2Priority = o2.getPriority();
		return listener1Priority.compareTo(listener2Priority);
	}

}
