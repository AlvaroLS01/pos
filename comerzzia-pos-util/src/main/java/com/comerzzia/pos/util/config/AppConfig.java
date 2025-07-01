package com.comerzzia.pos.util.config;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;

@Configuration
@Lazy(false)
public class AppConfig {
    private static final Logger log = Logger.getLogger(AppConfig.class);

	protected static AppConfigData configData;
		
	@Bean
	@DependsOn("dataSource")
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) {
		log.info("********************** OBTENIENDO SQLSESSIONFACTORY **********************");
		return MybatisSqlSessionFactoryBuilder.getSqlSessionFactory(dataSource);

    }
	        
    public static AppConfigData getCurrentConfiguration() {
    	if (configData == null) {
    		throw new RuntimeException("Spring context not initialized");    		
    	}
    	return configData;
    }
    
    public static void setConfigData(AppConfigData newConfigData) {
    	configData = newConfigData;
    	
    	addSystemProperties();
    	
    	log.info("Configuration loaded: " + configData);
    }
                
    protected static void addSystemProperties() {
		Map<String, String> properties = configData.getSystemProperties().getProperties();
		
		for (Entry<String, String> entry : properties.entrySet()) {
			log.debug("Setting system property " + entry.getKey() + " to " + entry.getValue());
			
			System.setProperty(entry.getKey(), entry.getValue());
		}
		
		if (log.isDebugEnabled()) {
			Properties props = System.getProperties();
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			pw.println();
			for (Enumeration<Object> e = props.keys() ; e.hasMoreElements() ;) {
	            String key = (String)e.nextElement();
	            String val = (String)props.get(key);
	            pw.println(key + "=" + val);
	        }
			StringBuffer sb = sw.getBuffer();
			log.debug("Listing system properties:");
			log.debug(sb);			
		}

	}
    
}
