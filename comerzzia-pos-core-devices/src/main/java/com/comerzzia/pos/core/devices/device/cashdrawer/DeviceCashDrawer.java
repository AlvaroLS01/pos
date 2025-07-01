


package com.comerzzia.pos.core.devices.device.cashdrawer;

import com.comerzzia.pos.core.devices.device.Device;



public interface DeviceCashDrawer extends Device{
    
    public void open();
    
    default public boolean isOpened() {
    	return false;
    }
    
}
