package com.comerzzia.pos.core.services.config;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.extern.log4j.Log4j;

@Log4j
public class EnvironmentSelector {
    protected static Environment currentEnvironment;
    
    protected static Map<Object, Environment> environments = new LinkedHashMap<>();
    
	public static Environment getCurrentEnvironment() {
		return currentEnvironment;
	}

	public static void setCurrentEnvironment(Environment environment) {
		if (environments.get(environment.getName()) == null) {
			throw new RuntimeException("Invalid environment: " + environment);
		}
		log.info("Activating environment: " + environment.getName());
		
		EnvironmentSelector.currentEnvironment = environment;
	}
	
	public static void addEnvironment(Environment environment) {
		environments.put(environment.getName(), environment);
		
		// activate default environment
		if (environment.getName().equals(Environment.DEFAULT_ENVIRONMENT)) {
			setCurrentEnvironment(environment);
		}
	}
	
    protected static Map<Object, Object> getDataSources() {
    	Map<Object, Object> dataSources = new LinkedHashMap<>();
    	
    	environments.forEach((k, v) -> {
    		dataSources.put(k, v.getDataSource());
    	});
    	
    	return dataSources;
    }
    
    public static Map<Object, Environment> getAvailableEnvironments(){
    	return environments;
    }
    
    public static boolean isDefaultEnvironment() {
    	return currentEnvironment.isDefault();
    }
    
    public static boolean isContingenceEnvironment() {
    	return currentEnvironment.isContingence();
    }
    
    public static boolean isTrainingEnvironment() {
    	return currentEnvironment.isTraining();
    }
    
    public static boolean isDefaultEnvironmentAvailable() {
    	return environments.containsKey(Environment.DEFAULT_ENVIRONMENT);
    }    
    
    public static boolean isContingenceEnvironmentAvailable() {
    	return environments.values().stream().anyMatch(env -> env.isContingence());
    }
    
    public static boolean isTrainingEnvironmentAvailable() {
    	return environments.values().stream().anyMatch(env -> env.isTraining());
    }
        
    public static void activateDefaultEnvironment() {
    	if (!isDefaultEnvironmentAvailable()) {
    		throw new IllegalAccessError("Default environment not available");
    	}
    	setCurrentEnvironment(environments.get(Environment.DEFAULT_ENVIRONMENT));
    }
    
}
