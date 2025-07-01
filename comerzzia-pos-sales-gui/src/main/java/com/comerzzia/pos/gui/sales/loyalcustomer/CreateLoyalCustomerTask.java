package com.comerzzia.pos.gui.sales.loyalcustomer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.api.loyalty.client.LyCustomersApiClient;
import com.comerzzia.api.loyalty.client.model.LyCustomerDetail;
import com.comerzzia.api.loyalty.client.model.NewLyCustomer;
import com.comerzzia.pos.core.gui.RestBackgroundTask;

import javafx.stage.Stage;

@Component
@Scope("prototype")
public class CreateLoyalCustomerTask extends RestBackgroundTask<LyCustomerDetail>{

	@Autowired
	protected LyCustomersApiClient customersApiClient;
	
	protected NewLyCustomer newCustomer;
	
	public CreateLoyalCustomerTask(NewLyCustomer newCustomer, Callback<LyCustomerDetail> callback, Stage stage) {
		super(callback, stage);
		this.newCustomer = newCustomer;
	}
	
	@Override
	protected LyCustomerDetail execute() throws Exception {		
		return customersApiClient.createLyCustomer(newCustomer).getBody();
	}

}
