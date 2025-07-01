package com.comerzzia.pos.gui.sales.loyalcustomer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.api.loyalty.client.LyCustomersApiClient;
import com.comerzzia.api.loyalty.client.model.LyCustomerDetail;
import com.comerzzia.pos.core.gui.RestBackgroundTask;

import javafx.stage.Stage;

@Component
@Scope("prototype")
public class QueryLoyalCustomerByIdTask extends RestBackgroundTask<LyCustomerDetail>{

	@Autowired
	protected LyCustomersApiClient customersApiClient;
	
	protected Long lyCustomerId;
	
	public QueryLoyalCustomerByIdTask(Long lyCustomerId, Callback<LyCustomerDetail> callback, Stage stage) {
		super(callback, stage);
		this.lyCustomerId = lyCustomerId;
	}

	@Override
	protected LyCustomerDetail execute() throws Exception {
		return customersApiClient.findLyCustomerById(lyCustomerId).getBody();
	}

}
