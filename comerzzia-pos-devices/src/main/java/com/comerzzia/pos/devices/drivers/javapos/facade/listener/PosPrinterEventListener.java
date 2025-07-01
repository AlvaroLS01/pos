
package com.comerzzia.pos.devices.drivers.javapos.facade.listener;

import jpos.events.DirectIOListener;
import jpos.events.ErrorListener;
import jpos.events.OutputCompleteListener;
import jpos.events.StatusUpdateListener;

public interface PosPrinterEventListener extends StatusUpdateListener,OutputCompleteListener,DirectIOListener,ErrorListener
{

}
