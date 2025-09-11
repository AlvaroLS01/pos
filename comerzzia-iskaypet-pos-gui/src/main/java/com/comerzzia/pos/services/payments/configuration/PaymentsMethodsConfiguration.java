package com.comerzzia.pos.services.payments.configuration;

import java.util.List;

public interface PaymentsMethodsConfiguration {
	
	List<PaymentMethodConfiguration> getPaymentsMethodsConfiguration();
	
	void loadConfiguration();
	
	void saveConfiguration() throws Exception;

}
