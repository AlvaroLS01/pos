package com.comerzzia.api.loyalty.persistence.civilStates;

import com.comerzzia.core.util.base.MantenimientoBean;

public class EstadoCivilKey extends MantenimientoBean {

	private static final long serialVersionUID = -5589789612046289075L;

	private String uidInstancia;

    private String codestcivil;

    public String getUidInstancia() {
        return uidInstancia;
    }

    public void setUidInstancia(String uidInstancia) {
        this.uidInstancia = uidInstancia == null ? null : uidInstancia.trim();
    }

    public String getCodestcivil() {
        return codestcivil;
    }

    public void setCodestcivil(String codestcivil) {
        this.codestcivil = codestcivil == null ? null : codestcivil.trim();
    }

	@Override
	protected void initNuevoBean() {
	}
}