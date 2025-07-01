


package com.comerzzia.pos.devices.cashdrawer;

import com.comerzzia.pos.core.devices.Devices;
import com.comerzzia.pos.core.devices.configuration.DeviceConfiguration;
import com.comerzzia.pos.core.devices.device.DeviceAbstractImpl;
import com.comerzzia.pos.core.devices.device.cashdrawer.DeviceCashDrawer;

/**
 *
 * El cajon impresora Envía la señal de apertura de cajón configurada a la impresora 1 configurada
 */
public class DeviceCashDrawerInPrinter extends DeviceAbstractImpl implements DeviceCashDrawer {

  
    @Override
    public void connect() {
        
    }

    @Override
    public void disconnect() {
        
    }

    @Override
    public void open() {
    	 Devices.getInstance().getPrinter1().initialize();
    	 Devices.getInstance().getPrinter1().openCashDrawer();
    	 Devices.getInstance().getPrinter1().endDocument();
    }

	@Override
	protected void loadConfiguration(DeviceConfiguration config) {
	}

}
