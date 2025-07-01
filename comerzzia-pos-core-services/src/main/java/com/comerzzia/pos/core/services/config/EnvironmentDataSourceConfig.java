package com.comerzzia.pos.core.services.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Lazy(false)
@Configuration
public class EnvironmentDataSourceConfig {
	@Bean
	public EnvironmentProperties environmentProperties() {
		return new EnvironmentProperties();
	}

    @Bean
    public DataSource dataSource(EnvironmentProperties environmentProperties) {
        EnvironmentRoutingDataSource tenantAwareRoutingDataSource = new EnvironmentRoutingDataSource();
        tenantAwareRoutingDataSource.setTargetDataSources(EnvironmentSelector.getDataSources());
        tenantAwareRoutingDataSource.afterPropertiesSet();
        return tenantAwareRoutingDataSource;
    }
}
