package com.comerzzia.pos.core.services.config;

import org.audit4j.core.AuditManager;
import org.audit4j.core.IAuditManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = "com.comerzzia.audit.enabled", havingValue = "true")
public class AuditConfig {

	@Bean
	public IAuditManager getAuditManager() {
	   return AuditManager.getInstance();
	}
}
