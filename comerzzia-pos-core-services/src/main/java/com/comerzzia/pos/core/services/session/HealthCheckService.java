package com.comerzzia.pos.core.services.session;

import java.util.Observer;

public interface HealthCheckService {

	void checkStatus();

	Integer getStatusId();

	String getStatusMessage();
	
	void addObserver(Observer o);
	
	void deleteObserver(Observer o);

	String getStatus();

	void close();
}