package com.comerzzia.pos.services.core.listeners;

import java.util.Comparator;


public class ComparatorPriorityListener implements Comparator<POSListener> {

	@Override
    public int compare(POSListener o1, POSListener o2) {
		Integer prioridadListener1 = o1.getProridad();
		Integer prioridadListener2 = o2.getProridad();
	    return prioridadListener1.compareTo(prioridadListener2);
    }

}
