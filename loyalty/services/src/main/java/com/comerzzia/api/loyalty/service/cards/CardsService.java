package com.comerzzia.api.loyalty.service.cards;

import java.util.List;

import com.comerzzia.api.core.service.exception.ApiException;
import com.comerzzia.api.core.service.exception.NotFoundException;
import com.comerzzia.api.loyalty.persistence.cards.CardDTO;
import com.comerzzia.api.loyalty.persistence.cards.CardExample;
import com.comerzzia.api.loyalty.persistence.cards.CardKey;
import com.comerzzia.api.loyalty.persistence.cards.CardUK;
import com.comerzzia.api.loyalty.persistence.cards.Card;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

public interface CardsService {
	public final String ACCOUNT_NOT_FOUND = "cards.ACCOUNT_NOT_FOUND";
	public final String EQUALS_ORIGIN_AND_DESTINATION = "cards.EQUALS_ORIGIN_AND_DESTINATION";
	public final String ALREADY_LINKED = "cards.ALREADY_LINKED";
	
	List<Card> selectByExample(CardExample example);
	List<CardDTO> selectDTOByExample(CardExample example);

	Card selectByPrimaryKey(CardKey key) throws NotFoundException;
	CardDTO selectDTOByPrimaryKey(CardKey key) throws NotFoundException;
	
	Card selectByUniqueKey(CardUK key) throws NotFoundException;
	CardDTO selectDTOByUniqueKey(CardUK key) throws NotFoundException;
	
	CardDTO selectByCardNumber(String cardNumber, IDatosSesion datosSesion) throws NotFoundException;	

	void insert(Card record) throws ApiException;
	
	Card updateByPrimaryKey(Card record) throws ApiException;

	void deactivate(CardKey key) throws ApiException;		
}
