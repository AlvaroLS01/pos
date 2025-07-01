package com.comerzzia.pos.core.devices.device.fiscal;

import java.util.List;

import com.comerzzia.pos.core.devices.configuration.DeviceConfiguration;
import com.comerzzia.pos.core.devices.device.DeviceAbstractImpl;
import com.comerzzia.pos.core.devices.device.DeviceException;

import javafx.stage.Stage;

public class DeviceFiscalDummy extends DeviceAbstractImpl implements DeviceFiscal{

	@Override
	public void connect() throws DeviceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disconnect() throws DeviceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public DeviceConfiguration getConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setConfiguration(DeviceConfiguration config) throws DeviceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getState() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setState(int estado) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isConfigurable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void configure(Stage stage) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public boolean isCanBeReset() {
		return false;
	}

	@Override
	public boolean reset() {
		return false;
	}

	@Override
	public List<String> getAvailabilityErrors() {
		return null;
	}

	@Override
	public String getAvailabilityErrorsAsAstring() {
		return null;
	}

	@Override
	protected void loadConfiguration(DeviceConfiguration config) throws DeviceException {
		// TODO Auto-generated method stub
		
	}

}
