package com.comerzzia.pos.core.services.balancecard;

import javax.validation.ValidationException;

import com.comerzzia.api.loyalty.client.model.Card;
import com.comerzzia.core.commons.exception.ApiException;
import com.comerzzia.omnichannel.facade.model.basket.BasketPromotable;
import com.comerzzia.omnichannel.facade.model.basket.header.BasketBalanceCard;

public interface BalanceCardService {

	Card recharge(String uidTransaccion, String cardCode, BasketPromotable<?> ticket)
			throws ApiException;

	Card returnBalance(String uidTransaccion, String cardCode, BasketPromotable<?> ticket) 
			throws ApiException;

	BasketBalanceCard findActiveBalanceCard(String balanceCardNumber) throws ValidationException, ApiException;

	boolean isBalanceCardItem(String codArticulo);

	BasketBalanceCard findBalanceCard(String balanceCardNumber) throws ApiException;
	
}