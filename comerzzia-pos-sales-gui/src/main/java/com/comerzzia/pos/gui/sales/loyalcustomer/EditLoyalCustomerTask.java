package com.comerzzia.pos.gui.sales.loyalcustomer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.api.loyalty.client.LyCustomersApiClient;
import com.comerzzia.api.loyalty.client.model.LyCustomerDetail;
import com.comerzzia.api.loyalty.client.model.UpdateLyCustomer;
import com.comerzzia.pos.core.gui.RestBackgroundTask;

import javafx.stage.Stage;

@Component
@Scope("prototype")
public class EditLoyalCustomerTask extends RestBackgroundTask<LyCustomerDetail>{
	
	@Autowired
	protected LyCustomersApiClient customersApiClient;
	
	protected Long lyCustomerId; 
	protected UpdateLyCustomer updateCustomer;
	
	public EditLoyalCustomerTask(Long lyCustomerId, UpdateLyCustomer updateCustomer, Callback<LyCustomerDetail> callback, Stage stage) {
		super(callback, stage);
		this.lyCustomerId = lyCustomerId;
		this.updateCustomer = updateCustomer;
	}

	@Override
	protected LyCustomerDetail execute() throws Exception {
		return customersApiClient.updateLyCustomer(lyCustomerId, updateCustomer).getBody();
	}

}
