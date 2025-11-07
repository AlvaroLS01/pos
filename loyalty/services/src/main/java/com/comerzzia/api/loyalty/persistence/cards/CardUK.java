package com.comerzzia.api.loyalty.persistence.cards;

import com.comerzzia.api.core.persistence.MultiInstance;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

public class CardUK extends MultiInstance {
	private static final long serialVersionUID = 1L;
	
    private String numeroTarjeta;
    
	public CardUK(IDatosSesion datosSesion, String cardNumber) {
		super(datosSesion);
		this.setNumeroTarjeta(cardNumber);
	}

	public String getNumeroTarjeta() {
		return numeroTarjeta;
	}

	public void setNumeroTarjeta(String cardNumber) {
		this.numeroTarjeta = cardNumber;
	}


}