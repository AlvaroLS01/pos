package com.comerzzia.pos.core.devices.device.mobilephonerecharge;

import com.comerzzia.pos.core.devices.configuration.DeviceConfiguration;
import com.comerzzia.pos.core.devices.device.DeviceAbstractImpl;
import com.comerzzia.pos.core.devices.device.DeviceException;

public class DeviceMobilePhoneRechargeDummy extends DeviceAbstractImpl implements DeviceMobilePhoneRecharge{

	@Override
	public void connect() throws DeviceException {
	}

	@Override
	public void disconnect() throws DeviceException {
	}

	@Override
	protected void loadConfiguration(DeviceConfiguration config) throws DeviceException {
		
	}

}
