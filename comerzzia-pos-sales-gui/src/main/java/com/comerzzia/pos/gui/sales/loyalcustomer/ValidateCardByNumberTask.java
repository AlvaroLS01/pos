package com.comerzzia.pos.gui.sales.loyalcustomer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.api.loyalty.client.CardsApiClient;
import com.comerzzia.pos.core.gui.RestBackgroundTask;

import javafx.stage.Stage;

@Component
@Scope("prototype")
public class ValidateCardByNumberTask extends RestBackgroundTask<Void>{
	
	@Autowired
	protected CardsApiClient cardsApiClient;
	
	protected String cardNumber;
	protected List<String> cardTypeCodes;

	public ValidateCardByNumberTask(String cardNumber, List<String> cardTypeCodes, Callback<Void> callback, Stage stage) {
		super(callback, stage);
		this.cardNumber = cardNumber;
		this.cardTypeCodes = cardTypeCodes;
	}

	@Override
	protected Void execute() throws Exception {
		return cardsApiClient.validateCardNumber(cardNumber, cardTypeCodes).getBody();
	}

}
