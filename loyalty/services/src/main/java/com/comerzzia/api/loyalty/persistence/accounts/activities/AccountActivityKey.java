package com.comerzzia.api.loyalty.persistence.accounts.activities;

import com.comerzzia.api.core.persistence.MultiInstance;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

public class AccountActivityKey extends MultiInstance {

	private static final long serialVersionUID = 711732479029191914L;
	private Long idCuentaMovimiento;
    
	public AccountActivityKey() {
    	
    }
        
	public AccountActivityKey(IDatosSesion datosSesion) {
		super(datosSesion);
	}

	public AccountActivityKey(IDatosSesion datosSesion, Long idCuentaMovimiento) {
		super(datosSesion);
		this.idCuentaMovimiento = idCuentaMovimiento;
	}
	


    public Long getIdCuentaMovimiento() {
        return idCuentaMovimiento;
    }

    public void setIdCuentaMovimiento(Long idCuentaMovimiento) {
        this.idCuentaMovimiento = idCuentaMovimiento;
    }
}