package com.comerzzia.api.loyalty.persistence.cards;

import com.comerzzia.api.core.persistence.MultiInstance;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

public class CardKey extends MultiInstance {
	private static final long serialVersionUID = 1L;
	private Long idTarjeta;
    
    public CardKey() {
    	
    }
    
    public CardKey(IDatosSesion sessionData) {
    	super(sessionData);
    }
    
    public CardKey(IDatosSesion sessionData, Long cardId) {
    	super(sessionData);
    	this.idTarjeta = cardId;
    }

    public Long getIdTarjeta() {
        return idTarjeta;
    }

    public void setIdTarjeta(Long cardId) {
        this.idTarjeta = cardId;
    }
    
    public String toString() {
    	return idTarjeta.toString();
    }
}