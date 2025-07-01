package com.comerzzia.pos.gui.sales.loyalcustomer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.api.loyalty.client.LyCustomerCardsApiClient;
import com.comerzzia.api.loyalty.client.model.LyCustomerCardDetail;
import com.comerzzia.pos.core.gui.RestBackgroundTask;

import javafx.stage.Stage;

@Component
@Scope("prototype")
public class QueryLoyalCustomerCardsTask extends RestBackgroundTask<List<LyCustomerCardDetail>>{
	
	//TODO Consultar filtrada
	
	@Autowired
	protected LyCustomerCardsApiClient custCardsApiClient;
	
	protected Long lyCustomerId;
	
	public QueryLoyalCustomerCardsTask(Long lyCustomerId, Callback<List<LyCustomerCardDetail>> callback, Stage stage) {
		super(callback, stage);
		this.lyCustomerId = lyCustomerId;
	}

	@Override
	protected List<LyCustomerCardDetail> execute() throws Exception {
		return custCardsApiClient.findLyCustomerCards(lyCustomerId).getBody();
	}

}
