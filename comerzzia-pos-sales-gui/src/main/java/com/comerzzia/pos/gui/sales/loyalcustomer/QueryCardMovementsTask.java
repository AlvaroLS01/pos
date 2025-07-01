package com.comerzzia.pos.gui.sales.loyalcustomer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.api.loyalty.client.AccountTransactionsApiClient;
import com.comerzzia.api.loyalty.client.model.AccountTransaction;
import com.comerzzia.pos.core.gui.RestBackgroundTask;

import javafx.stage.Stage;

@Component
@Scope("prototype")
public class QueryCardMovementsTask extends RestBackgroundTask<List<AccountTransaction>>{
	
	@Autowired
	protected AccountTransactionsApiClient accTransactionsApiClient;
	
	protected Long cardAccountId;
	protected Long cardId;
	protected Integer lastAccountTransactions;
	
	public QueryCardMovementsTask(Long cardAccountId, Long cardId, Integer lastAccountTransactions, Callback<List<AccountTransaction>> callback, Stage stage) {
		super(callback, stage);
		this.cardAccountId = cardAccountId;
		this.cardId = cardId;
		this.lastAccountTransactions = lastAccountTransactions;
	}

	@Override
	protected List<AccountTransaction> execute() throws Exception {
		return accTransactionsApiClient.findAccountTransactionsList(cardAccountId, false, false, false, false, cardId, null, null, null, lastAccountTransactions).getBody();
	}

}
