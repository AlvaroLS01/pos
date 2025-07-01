package com.comerzzia.pos.core.services.config;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.core.io.ClassPathResource;

import com.comerzzia.pos.util.config.TPVConfig;

import lombok.extern.log4j.Log4j;

@Log4j
@ConfigurationProperties(prefix = "environments")
public class EnvironmentProperties {
    private Map<Object, Object> properties = new LinkedHashMap<>();

    public Map<Object, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Map<String, String>> properties) {
    	for (Entry<String, Map<String, String>> entry : properties.entrySet()) {
    		log.info("Loading environment " + entry.getKey() + "...");
    		
    		Environment environment;
    		
    		try {
    			environment = new Environment(entry.getKey());    			    		
    		} catch(IllegalArgumentException ignore) {
    			log.error("Invalid environment key: " + entry.getKey());
    			continue;
    		}

    		environment.setProperties(entry.getValue());    		
    		environment.setConfig(loadTillConfiguration(environment));
    		setEnvironmentProperties(environment);
    		
    		if (environment.getConfig() == null) continue;
    		
    		if (entry.getValue().containsKey("driverClassName")) {    		
    			environment.setDataSource(createDataSource(entry.getValue()));
    		}
    		
    		EnvironmentSelector.addEnvironment(environment);
    		
    		properties.put(entry.getKey(), entry.getValue());
    	}
    }
    
    protected DataSource createDataSource(Map<String, String> source) {
        return DataSourceBuilder.create()
                .url(source.get("url"))
                .driverClassName(source.get("driverClassName"))
                .username(source.get("username"))
                .password(source.get("password"))
                .build();
    }
    
    protected TPVConfig loadTillConfiguration(Environment environment) {
    	String fileName = environment.getTillConfigurationFile();
    	
		ClassPathResource configFile = new ClassPathResource(fileName);
		TPVConfig config = null;
		
		if (configFile.exists()) {
			try {
				log.info("Loading TILL configuration file from " + configFile.getDescription() + " : " + configFile.getURL().toString());
				
				JAXBContext jaxbContext = JAXBContext.newInstance(TPVConfig.class);
				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();  
				config= (TPVConfig) jaxbUnmarshaller.unmarshal(configFile.getFile());
				
				log.info("Loaded TILL configuration : " + config);
			} catch (Exception e) {
				log.error("Error reading POS TILL configuration file: " + e.getMessage(), e);
			}  	          	
		} else {
			log.error(String.format("Till configuration file %s not found", configFile.getDescription()));
			if(StringUtils.equalsIgnoreCase(environment.getName(), Environment.DEFAULT_ENVIRONMENT)) {
				throw new ConfigurationException(String.format("Till configuration file %s not found", configFile.getDescription()));
			}
		}
				
		return config;
	}
    
    protected void setEnvironmentProperties(Environment environment) {
    	String training = environment.getProperties().get("training");
    	String contingence = environment.getProperties().get("contingence");
    	if(StringUtils.equalsIgnoreCase(environment.getName(), Environment.DEFAULT_ENVIRONMENT)) {
    		environment.setDefault(true);
    		return;
    	}
    	if(Boolean.valueOf(training)) {
    		environment.setTraining(Boolean.valueOf(training));
    		return;
    	}
    	environment.setContingence(Boolean.valueOf(contingence));
    }
    	
}
