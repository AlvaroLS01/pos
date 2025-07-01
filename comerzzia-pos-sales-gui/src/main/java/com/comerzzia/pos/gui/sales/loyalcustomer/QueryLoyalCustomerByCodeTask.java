package com.comerzzia.pos.gui.sales.loyalcustomer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.api.loyalty.client.LyCustomersApiClient;
import com.comerzzia.pos.core.gui.RestBackgroundTask;

import javafx.stage.Stage;

@Component
@Scope("prototype")
public class QueryLoyalCustomerByCodeTask extends RestBackgroundTask<Boolean>{
	
	@Autowired
	protected LyCustomersApiClient customersApiClient;
	
	protected String lyCustomerCode;
	
	public QueryLoyalCustomerByCodeTask(String lyCustomerCode, Callback<Boolean> callback, Stage stage) {
		super(callback, stage);
		this.lyCustomerCode = lyCustomerCode;
	}

	@Override
	protected Boolean execute() throws Exception {
		return customersApiClient.findLyCustomerPage(null, lyCustomerCode, null, null, null, null, null, null, null, null, null).getBody().size() != 0;
	}

}
