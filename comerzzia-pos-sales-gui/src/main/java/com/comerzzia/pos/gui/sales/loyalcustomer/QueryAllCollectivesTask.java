package com.comerzzia.pos.gui.sales.loyalcustomer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.api.loyalty.client.CollectivesApiClient;
import com.comerzzia.api.loyalty.client.model.Collective;
import com.comerzzia.pos.core.gui.RestBackgroundTask;

import javafx.stage.Stage;

@Component
@Scope("prototype")
public class QueryAllCollectivesTask extends RestBackgroundTask<List<Collective>>{
	
	@Autowired
	protected CollectivesApiClient collectivesApiClient;
	
	protected Boolean privated;
	
	public QueryAllCollectivesTask(Boolean privated, Callback<List<Collective>> callback, Stage stage) {
		super(callback, stage);
		this.privated = privated;
	}

	@Override
	protected List<Collective> execute() throws Exception {
		return collectivesApiClient.findAllCollectives(privated).getBody();
	}
	
}
