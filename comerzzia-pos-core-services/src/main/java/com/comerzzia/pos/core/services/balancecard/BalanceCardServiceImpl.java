package com.comerzzia.pos.core.services.balancecard;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.ValidationException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.api.loyalty.client.AccountTransactionsApiClient;
import com.comerzzia.api.loyalty.client.CardsApiClient;
import com.comerzzia.api.loyalty.client.model.Card;
import com.comerzzia.api.loyalty.client.model.CardTransaction;
import com.comerzzia.core.commons.exception.ApiException;
import com.comerzzia.core.facade.service.variable.VariableServiceFacade;
import com.comerzzia.omnichannel.facade.model.basket.BasketPromotable;
import com.comerzzia.omnichannel.facade.model.basket.header.BasketBalanceCard;
import com.comerzzia.omnichannel.facade.model.payments.PaymentMethodConfiguration;
import com.comerzzia.omnichannel.facade.model.payments.StorePosPaymentMethods;
import com.comerzzia.omnichannel.facade.service.store.StorePosPaymentMethodServiceFacade;
import com.comerzzia.pos.core.services.session.Session;
import com.comerzzia.pos.util.i18n.I18N;

@Service
public class BalanceCardServiceImpl implements BalanceCardService {

	protected Logger log = Logger.getLogger(BalanceCardServiceImpl.class);
	
	@Autowired
	protected Session session;
	
	@Autowired
	protected VariableServiceFacade variablesServices;
	
	@Autowired
	protected StorePosPaymentMethodServiceFacade storePosPaymentMethodService;
	
	@Autowired
	protected CardsApiClient cardsApiClient;
	
	@Autowired
	protected AccountTransactionsApiClient accTransactionsApiClient;

	protected List<String> balanceCardItemCodes;
	
	@Override
	public Card recharge(String uidTransaccion, String cardCode, BasketPromotable<?> ticket) throws ApiException {
		log.debug("recharge() - Recharging gift card with number: " + cardCode   + " and amount: " + ticket.getTotals().getTotalWithTaxes().abs());
		
		Card card = cardsApiClient.findCardByNumber(cardCode).getBody();
		
		CardTransaction newAccountTransaction = new CardTransaction();
		newAccountTransaction.setAccountTransactionDate(new Date());
		newAccountTransaction.setSalesDocUid(ticket.getBasketCode());
		newAccountTransaction.setConcept(ticket.getHeader().getDocTypeDes() + " " + ticket.getBasketCode());
		newAccountTransaction.setInput(ticket.getTotals().getTotalWithTaxes().abs());
		newAccountTransaction.setOutput(BigDecimal.ZERO);
		newAccountTransaction.setMovementStatusId(0L);
		newAccountTransaction.setTransactionUid(uidTransaccion);
		
		if(card.getActivationDate()!=null) {
			return cardsApiClient.addBalance(cardCode, newAccountTransaction).getBody();
		} else {
			return cardsApiClient.activateCard(cardCode, newAccountTransaction).getBody();
		}
	}
	
	@Override
	public Card returnBalance(String uidTransaccion, String cardCode, BasketPromotable<?> ticket) throws ApiException {
		log.debug("recharge() - Recharging gift card with number: " + cardCode + " and amount: " + ticket.getTotals().getTotalWithTaxes().abs());

		CardTransaction newAccountTransaction = new CardTransaction();
		newAccountTransaction.setAccountTransactionDate(new Date());
		newAccountTransaction.setSalesDocUid(ticket.getBasketCode());
		newAccountTransaction.setConcept(ticket.getHeader().getDocTypeDes() + " " + ticket.getBasketCode());
		newAccountTransaction.setInput(ticket.getTotals().getTotalWithTaxes().abs().negate());
		newAccountTransaction.setOutput(BigDecimal.ZERO);
		newAccountTransaction.setMovementStatusId(0L);
		newAccountTransaction.setTransactionUid(uidTransaccion);
		
		return cardsApiClient.removeBalance(cardCode, newAccountTransaction).getBody();
	}
	
	@Override
	public BasketBalanceCard findActiveBalanceCard(String balanceCardNumber) throws ValidationException, ApiException {
		BasketBalanceCard balanceCard = new BasketBalanceCard();
		Card card = getActiveBalanceCard(balanceCardNumber);
		
		balanceCard.setIsMoneyCard(card.getCardType().getPaymentsAllowed());
		balanceCard.setCardTypeCode(card.getCardType().getCardTypeCode());
		balanceCard.setCardCode(card.getCardNumber());
		
		if (card.getAccount() != null) {
			balanceCard.setBalance(card.getAccount().getBalance());
			balanceCard.setProvisionalBalance(card.getAccount().getProvisionalBalance());
		} else {
			balanceCard.setBalance(BigDecimal.ZERO);
			balanceCard.setProvisionalBalance(BigDecimal.ZERO);
		}

		return balanceCard;
	}

	@Override
	public BasketBalanceCard findBalanceCard(String balanceCardNumber) throws ApiException {
		BasketBalanceCard balanceCard = new BasketBalanceCard();
		Card card = getBalanceCard(balanceCardNumber);
		
		balanceCard.setIsMoneyCard(card.getCardType().getPaymentsAllowed());
		balanceCard.setCardTypeCode(card.getCardType().getCardTypeCode());
		balanceCard.setCardCode(card.getCardNumber());
		
		if (card.getAccount() != null) {
			balanceCard.setBalance(card.getAccount().getBalance());
			balanceCard.setProvisionalBalance(card.getAccount().getProvisionalBalance());
		} else {
			balanceCard.setBalance(BigDecimal.ZERO);
			balanceCard.setProvisionalBalance(BigDecimal.ZERO);
		}
		
		return balanceCard;
	}
	
	protected Card getActiveBalanceCard(String balanceCardNumber) throws ValidationException, ApiException {
		Card card = cardsApiClient.findCardByNumber(balanceCardNumber).getBody();
		
		Date now = new Date();
		if (card.getActivationDate() == null || card.getActivationDate().after(now) ||
				(card.getDeactivationDate() != null && card.getDeactivationDate().before(now))) {
			throw new ValidationException(I18N.getText("La tarjeta no está activa."));
		}
		
		return card;
	}

	protected Card getBalanceCard(String balanceCardNumber) throws ApiException {
		return cardsApiClient.findCardByNumber(balanceCardNumber).getBody();
	}
	
	@Override
	public boolean isBalanceCardItem(String codArticulo) {
		if(balanceCardItemCodes==null) {
			loadItemCodes();
		}
		return balanceCardItemCodes.contains(codArticulo);
	}
	
	protected void loadItemCodes() {
		balanceCardItemCodes = new ArrayList<String>();
		StorePosPaymentMethods paymentMethods = storePosPaymentMethodService.getPaymentMethods(session.getApplicationSession().getCodAlmacen(), session.getApplicationSession().getTillCode());
		for(PaymentMethodConfiguration paymentMehtodConfiguration : paymentMethods.getPaymentMethodConfigurations().values()) {
			String strItemCodes = paymentMehtodConfiguration.getGatewayConfigurationProperties().get("articulos_gift_card");
			if(StringUtils.isNotBlank(strItemCodes)) {
				String[] itemCodesSplit = strItemCodes.split(",");
				for(int i = 0 ; i < itemCodesSplit.length ; i++) {
					String itemCodeGiftCard = itemCodesSplit[i];
					itemCodeGiftCard = itemCodeGiftCard.trim();
					balanceCardItemCodes.add(itemCodeGiftCard);
				}
			}
		}
	}
}
