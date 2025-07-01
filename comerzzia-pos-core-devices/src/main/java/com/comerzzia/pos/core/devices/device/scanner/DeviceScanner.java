package com.comerzzia.pos.core.devices.device.scanner;

import java.util.Observer;

import com.comerzzia.pos.core.devices.device.Device;


public interface DeviceScanner extends Device {
	public static final String COMMAND_BEEP = "BEEP";
	public static final String COMMAND_BEEP_ERROR = "BEEP_ERROR";
	
	void enableReader();
	void disableReader();
	
	default void beep() {};
	default void beepError() {};
	
	void addObserver(Observer observer);
	void deleteObserver(Observer observer);
	void deleteObservers();

}
