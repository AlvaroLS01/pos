package com.comerzzia.pos.core.services.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class EnvironmentRoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected String determineCurrentLookupKey() {
        return EnvironmentSelector.getCurrentEnvironment().getName();
    }
}