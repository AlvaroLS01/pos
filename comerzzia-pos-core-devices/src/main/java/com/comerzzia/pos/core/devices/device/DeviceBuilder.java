
package com.comerzzia.pos.core.devices.device;

import org.apache.log4j.Logger;

import com.comerzzia.pos.core.devices.AvailableDevicesLoadException;
import com.comerzzia.pos.core.devices.configuration.DeviceModelConfiguration;

public class DeviceBuilder {
	
	private static Logger log = Logger.getLogger(DeviceBuilder.class);

	public Device create(DeviceModelConfiguration confModelo) throws AvailableDevicesLoadException{
		try {
			Class<?> forName = Class.forName(confModelo.getManager());
			Device device = (Device) forName.newInstance();
			return device;
		} catch (Throwable e) {
			log.error("create() - Error al inicializar clase con className: " + confModelo.getManager(), e);
			throw new AvailableDevicesLoadException(e);
		}
	}
	
}
