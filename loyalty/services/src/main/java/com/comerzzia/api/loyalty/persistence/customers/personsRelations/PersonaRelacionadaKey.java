package com.comerzzia.api.loyalty.persistence.customers.personsRelations;

import com.comerzzia.core.util.base.MantenimientoBean;

public class PersonaRelacionadaKey extends MantenimientoBean {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -2818844539456012858L;

	private String uidInstancia;

    private String uidRelacionFidelizado;

    public String getUidInstancia() {
        return uidInstancia;
    }

    public void setUidInstancia(String uidInstancia) {
        this.uidInstancia = uidInstancia == null ? null : uidInstancia.trim();
    }

    public String getUidRelacionFidelizado() {
        return uidRelacionFidelizado;
    }

    public void setUidRelacionFidelizado(String uidRelacionFidelizado) {
        this.uidRelacionFidelizado = uidRelacionFidelizado == null ? null : uidRelacionFidelizado.trim();
    }

	@Override
	protected void initNuevoBean() {}
}