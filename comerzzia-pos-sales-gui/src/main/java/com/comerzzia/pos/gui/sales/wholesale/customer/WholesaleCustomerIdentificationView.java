package com.comerzzia.pos.gui.sales.wholesale.customer;

import org.springframework.stereotype.Component;

import com.comerzzia.pos.gui.sales.retailidentified.customer.CustomerIdentificationView;

@Component
public class WholesaleCustomerIdentificationView extends CustomerIdentificationView {
	
	@Override
	protected String getFXMLName() {
		return getFXMLName(CustomerIdentificationView.class);
	}
	
}
