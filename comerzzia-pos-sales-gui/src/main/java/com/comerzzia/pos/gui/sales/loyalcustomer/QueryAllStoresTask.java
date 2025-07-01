package com.comerzzia.pos.gui.sales.loyalcustomer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.api.loyalty.client.StoresApiClient;
import com.comerzzia.api.loyalty.client.model.Store;
import com.comerzzia.pos.core.gui.RestBackgroundTask;

import javafx.stage.Stage;

@Component
@Scope("prototype")
public class QueryAllStoresTask extends RestBackgroundTask<List<Store>> {
	
	@Autowired
	protected StoresApiClient storesApiClient;
	
	public QueryAllStoresTask(Callback<List<Store>> callback, Stage stage) {
		super(callback, stage);
	}

	@Override
	protected List<Store> execute() throws Exception {
		return storesApiClient.findStores(null, null, null, null, null).getBody();
	}

}
