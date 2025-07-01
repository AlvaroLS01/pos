
package com.comerzzia.pos.core.devices.configuration;

import static com.comerzzia.pos.core.devices.Devices.TAG_CONFIG_PARAMS;
import static com.comerzzia.pos.core.devices.Devices.TAG_CONN_NAME;
import static com.comerzzia.pos.core.devices.Devices.TAG_MODEL;

import java.util.HashMap;
import java.util.Map;

import com.comerzzia.pos.util.xml.XMLDocumentNode;
import com.comerzzia.pos.util.xml.XMLDocumentNodeNotFoundException;

public class DeviceConfiguration {

    public static final String NO_CONFIGURED = "NO USA";
    
    private String mode = "";
    private String connectionName = "";
    
    private Map<String, String> configurationParameters;

	private String deviceConfigKey;
	
	public DeviceConfiguration() {
	}
    
    public DeviceConfiguration(String deviceConfigKey) {
    	this.deviceConfigKey = deviceConfigKey;
    }
    
    public DeviceConfiguration(String deviceConfigKey, XMLDocumentNode deviceConfig) throws XMLDocumentNodeNotFoundException{
        this.deviceConfigKey = deviceConfigKey;
		setModel(deviceConfig.getNode(TAG_MODEL).getValue());
        setConnectionName(deviceConfig.getNode(TAG_CONN_NAME).getValue());
        
        if(deviceConfig.getNode(TAG_CONFIG_PARAMS, true) != null) {
        	configurationParameters = new HashMap<String, String>();
        	for(XMLDocumentNode child : deviceConfig.getNode(TAG_CONFIG_PARAMS).getChildren()) {
        		configurationParameters.put(child.getName(), child.getValue());
        	}
        }
    }
    
    private DeviceModelConfiguration modelConfiguration;
    
    public String getModel() {
        return mode;
    }

    public void setModel(String model) {
        this.mode = model;
    }

    public String getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }
    
    public Map<String, String> getConfigurationParameters() {
    	if(configurationParameters == null) {
    		configurationParameters = new HashMap<String, String>();
    	}
    	return configurationParameters;
    }
	
    public void setConfigurationParameters(Map<String, String> configurationParameters) {
    	this.configurationParameters = configurationParameters;
    }

	public void setModelConfiguration(DeviceModelConfiguration modelConfiguration) {        
        this.modelConfiguration = modelConfiguration;
    }

	public DeviceModelConfiguration getModelConfiguration() {        
        return modelConfiguration;
    }
    
    public boolean isNotDummyDevice(){
        return !mode.equals(NO_CONFIGURED);
    }

	@Override
	public String toString() {
		return "[Model=" + mode + ", ConnectionName=" + connectionName + "]";
	}

	public String getDeviceConfigKey() {
	    return deviceConfigKey;
    }

	public void setDeviceConfigKey(String deviceConfigKey) {
	    this.deviceConfigKey = deviceConfigKey;
    }
    
}
