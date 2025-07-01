
package com.comerzzia.pos.core.devices.device.scale;

import java.math.BigDecimal;

import com.comerzzia.pos.core.devices.configuration.DeviceConfiguration;
import com.comerzzia.pos.core.devices.device.DeviceAbstractImpl;

public class DeviceScaleDummy extends DeviceAbstractImpl implements DeviceScale {

    @Override
    public void connect() {
        
    }

    @Override
    public void disconnect() {
        
    }

    @Override
    public Double getWeight(BigDecimal precio) {
        return 0.0;
    }

	@Override
	protected void loadConfiguration(DeviceConfiguration config) {
	}

}
