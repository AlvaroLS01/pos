

package com.comerzzia.pos.core.devices.device;

import java.util.List;
import java.util.Observable;

import org.apache.log4j.Logger;

import com.comerzzia.pos.core.devices.configuration.DeviceConfiguration;

import javafx.stage.Stage;

public abstract class DeviceAbstractImpl extends Observable implements Device {
	private static final Logger log = Logger.getLogger(DeviceAbstractImpl.class.getName());

	public DeviceConfiguration configuration;
	public int state;

	public abstract void connect() throws DeviceException;

	public abstract void disconnect() throws DeviceException;

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public DeviceConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(DeviceConfiguration config) throws DeviceException {
		this.configuration = config;
		if (config.getModelConfiguration() != null) {
			loadConfiguration(config);
		}
	}

	protected abstract void loadConfiguration(DeviceConfiguration config) throws DeviceException;

	@Override
	public String toString() {
		return configuration == null ? "[Configuracion: null]" : configuration.toString();
	}

	@Override
	public boolean isConfigurable() {
		return false;
	}

	@Override
	public void configure(Stage stage) {
	}

	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public boolean isCanBeReset() {
		return true;
	}

	@Override
	public boolean reset() {
		log.warn("reset() - Trying to reset the device");

		try {
			log.debug("reset() - Disconnecting the device");
			disconnect();
			log.debug("reset() - Connecting the device");
			connect();
		} catch (DeviceException e) {
			log.error("reset() - Reset device error: " + e.getMessage(), e);
		}

		return isReady();
	}
	
	@Override
	public List<String> getAvailabilityErrors() {
	    return null;
	}
	
	
	@Override
	public String getAvailabilityErrorsAsAstring() {
		StringBuilder errorString = new StringBuilder();
		
		List<String> errors = getAvailabilityErrors();
		
		if(errors != null && !errors.isEmpty()) {
			errors.forEach(message -> errorString.append(message));
		}
		
		return errorString.toString();
	}

}
