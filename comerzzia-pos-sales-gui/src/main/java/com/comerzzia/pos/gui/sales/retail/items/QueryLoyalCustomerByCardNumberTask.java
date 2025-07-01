package com.comerzzia.pos.gui.sales.retail.items;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.api.loyalty.client.LyCustomersApiClient;
import com.comerzzia.pos.core.gui.RestBackgroundTask;

import javafx.stage.Stage;

@Component
@Scope("prototype")
public class QueryLoyalCustomerByCardNumberTask extends RestBackgroundTask<Long>{
	
	@Autowired
	protected LyCustomersApiClient customersApiClient;
	
	protected String cardNumber;
	
	public QueryLoyalCustomerByCardNumberTask(String cardNumber, Callback<Long> callback, Stage stage) {
		super(callback, stage);
		this.cardNumber = cardNumber;
	}

	@Override
	protected Long execute() throws Exception {
		return customersApiClient.findLyCustomerByCard(cardNumber).getBody().getLyCustomerId();
	}

}
