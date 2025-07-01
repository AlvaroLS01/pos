package com.comerzzia.pos.gui.sales.balancecard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.omnichannel.facade.model.basket.header.BasketBalanceCard;
import com.comerzzia.pos.core.gui.RestBackgroundTask;
import com.comerzzia.pos.core.services.balancecard.BalanceCardService;

import javafx.stage.Stage;

@Component
@Scope("prototype")
public class QueryBalanceCardByCardNumberTask extends RestBackgroundTask<BasketBalanceCard> {
	
	@Autowired
    protected BalanceCardService balanceCardService;

	protected String cardNumber;
	protected Boolean active;
	
	public QueryBalanceCardByCardNumberTask(String cardNumber, Callback<BasketBalanceCard> callback, Stage stage) {
		super(callback, stage);
		this.cardNumber = cardNumber;
	}

	public QueryBalanceCardByCardNumberTask(String cardNumber, Boolean active, Callback<BasketBalanceCard> callback, Stage stage) {
		super(callback, stage);
		this.cardNumber = cardNumber;
		this.active = active;
	}


	@Override
	protected BasketBalanceCard execute() throws Exception {
		if(Boolean.TRUE.equals(active)) {
			return balanceCardService.findActiveBalanceCard(cardNumber);
		}else {
			return balanceCardService.findBalanceCard(cardNumber);
		}
	}

}
