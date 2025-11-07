package com.comerzzia.api.loyalty.persistence.accounts.activities;

public class AccountActivityUK {
	
	private String uidTransaccion;
    
	public AccountActivityUK(String uidTransaccion) {
		this.setUidTransaccion(uidTransaccion);
	}

	public String getUidTransaccion() {
		return uidTransaccion;
	}

	public void setUidTransaccion(String uidTransaccion) {
		this.uidTransaccion = uidTransaccion;
	}

}
