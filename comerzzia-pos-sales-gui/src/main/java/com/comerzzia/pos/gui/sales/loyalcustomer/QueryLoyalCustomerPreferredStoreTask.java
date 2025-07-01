package com.comerzzia.pos.gui.sales.loyalcustomer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.api.loyalty.client.LyCustomerFavoriteStoresApiClient;
import com.comerzzia.api.loyalty.client.model.LyCustomerFavoriteStore;
import com.comerzzia.pos.core.gui.RestBackgroundTask;

import javafx.stage.Stage;

@Component
@Scope("prototype")
public class QueryLoyalCustomerPreferredStoreTask extends RestBackgroundTask<LyCustomerFavoriteStore>{
	
	@Autowired
	protected LyCustomerFavoriteStoresApiClient favStoreApiClient;
	
	protected Long lyCustomerId;

	public QueryLoyalCustomerPreferredStoreTask(Long lyCustomerId, Callback<LyCustomerFavoriteStore> callback, Stage stage) {
		super(callback, stage);
		this.lyCustomerId = lyCustomerId;
	}

	@Override
	protected LyCustomerFavoriteStore execute() throws Exception {
		return favStoreApiClient.findLyCustomerFavoriteStore(lyCustomerId).getBody();
	}

}
