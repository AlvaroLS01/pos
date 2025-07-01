package com.comerzzia.pos.gui.sales.loyalcustomer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.api.loyalty.client.CivilStatusApiClient;
import com.comerzzia.api.loyalty.client.model.CivilStatus;
import com.comerzzia.pos.core.gui.RestBackgroundTask;

import javafx.stage.Stage;

@Component
@Scope("prototype")
public class QueryCivilStatusTask extends RestBackgroundTask<List<CivilStatus>>{

	@Autowired
	protected CivilStatusApiClient civilStatusApiClient;
	
	public QueryCivilStatusTask(Callback<List<CivilStatus>> callback, Stage stage) {
		super(callback, stage);
	}

	@Override
	protected List<CivilStatus> execute() throws Exception {
		return civilStatusApiClient.findAllCivilStatus().getBody();
	}

}
