package com.comerzzia.pos.gui.sales.loyalcustomer;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.api.loyalty.client.LyCustomerPurchasesApiClient;
import com.comerzzia.api.loyalty.client.model.LyCustomerPurchaseDetail;
import com.comerzzia.pos.core.gui.RestBackgroundTask;

import javafx.stage.Stage;

@Component
@Scope("prototype")
public class QueryLoyalCustomerLatestSalesTask extends RestBackgroundTask<List<LyCustomerPurchaseDetail>>{

	@Autowired
	protected LyCustomerPurchasesApiClient custPurchasesApiClient;
	
	protected Long lyCustomerId;
	protected Date fromDate;
	protected Date toDate;
	
	public QueryLoyalCustomerLatestSalesTask(Long lyCustomerId, Date fromDate, Date toDate, Callback<List<LyCustomerPurchaseDetail>> callback, Stage stage) {
		super(callback, stage);
		this.lyCustomerId = lyCustomerId;
		this.fromDate = fromDate;
		this.toDate = toDate;
	}

	@Override
	protected List<LyCustomerPurchaseDetail> execute() throws Exception {
		return custPurchasesApiClient.findLyCustomerPurchasesItems(lyCustomerId, fromDate, toDate).getBody();
	}

}
