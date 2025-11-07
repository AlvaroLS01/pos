package com.comerzzia.api.loyalty.persistence.accounts;

import java.util.List;

import com.comerzzia.api.loyalty.persistence.cards.Card;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class AccountDTO extends Account{

	private static final long serialVersionUID = -112790363362840632L;
	
	@JsonInclude(Include.NON_NULL)
	private List<Card> tarjetas;

	  
	public List<Card> getTarjetas() {
		return tarjetas;
	}

	public void setTarjetas(List<Card> tarjetas) {
		this.tarjetas = tarjetas;
	}
	
}
