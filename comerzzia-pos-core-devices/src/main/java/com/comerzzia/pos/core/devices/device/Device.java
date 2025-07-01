
package com.comerzzia.pos.core.devices.device;

import java.util.List;
import java.util.Observer;

import com.comerzzia.pos.core.devices.configuration.DeviceConfiguration;

import javafx.stage.Stage;

public interface Device {

	public void connect() throws DeviceException;
	
    public void disconnect() throws DeviceException;
    
    public DeviceConfiguration getConfiguration();
    
    public void setConfiguration(DeviceConfiguration config) throws DeviceException;
    
    public int getState();
    
	public void setState(int state);
	
	public boolean isConfigurable();
	
	public void configure(Stage stage);
	
	public boolean isReady();
	
	public boolean isCanBeReset();
	
	public boolean reset();
	
	public List<String> getAvailabilityErrors();
	
	void addObserver(Observer observer);
	void deleteObserver(Observer observer);
	void deleteObservers();

	String getAvailabilityErrorsAsAstring();
	
}
