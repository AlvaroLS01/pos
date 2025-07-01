package com.comerzzia.pos.core.services.config;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.comerzzia.pos.util.config.TPVConfig;

public class Environment {
	
	public static final String DEFAULT_ENVIRONMENT = "DEFAULT";
	
    protected String name;
    protected Map<String, String> properties;
    protected TPVConfig config;
    protected boolean isDefault = false;
    protected boolean isContingence = false;
    protected boolean isTraining = false;
    protected Object dataSource;
           
    public Environment(String name) {
       this.name = name;	
    }

	public String getName() {
		return name;
	}

	public TPVConfig getConfig() {
		return config;
	}

	public void setConfig(TPVConfig config) {
		this.config = config;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	public Object getDataSource() {
		return dataSource;
	}

	public void setDataSource(Object dataSource) {
		this.dataSource = dataSource;
	}

	public String getApiUrl() {
		if (properties == null || !properties.containsKey("apiUrl")) {
			return null;	
		}
		return properties.get("apiUrl");
		
	}
	
	public String getTillConfigurationFile() {
		if (properties == null || !properties.containsKey("tillConfigurationFile")) {
			boolean defaultEnvironment = StringUtils.equalsIgnoreCase(getName(), Environment.DEFAULT_ENVIRONMENT);
			String prefix = "";
	    	
	    	if (!defaultEnvironment) {
	    		prefix = getName() + "_";
	    	}
	    	
	    	return prefix + "pos_config.xml";
		}
		return properties.get("tillConfigurationFile");		
	}

	public boolean isDefault() {
		return isDefault;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	public boolean isContingence() {
		return isContingence;
	}

	public void setContingence(boolean isContingence) {
		this.isContingence = isContingence;
	}

	public boolean isTraining() {
		return isTraining;
	}

	public void setTraining(boolean isTraining) {
		this.isTraining = isTraining;
	}

	public String toString() {
		return name;
	}
	
}
