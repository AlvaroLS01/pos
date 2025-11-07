package com.comerzzia.api.loyalty.persistence.cardsTypes;

import com.comerzzia.core.model.i18n.InternacionalizableBean;

public class TipoTarjetaKey extends InternacionalizableBean {

	private static final long serialVersionUID = -2489801737173647594L;

	private String uidInstancia;
	
    private String codtipotarj;

    public TipoTarjetaKey() {}
    
    public TipoTarjetaKey(String uidInstancia, String codtipotarj) {
    	this.uidInstancia = uidInstancia;
    	this.codtipotarj = codtipotarj;
    }
    
    public String getUidInstancia() {
        return uidInstancia;
    }

    public void setUidInstancia(String uidInstancia) {
        this.uidInstancia = uidInstancia == null ? null : uidInstancia.trim();
    }

	public String getCodtipotarj() {
        return codtipotarj;
    }

    public void setCodtipotarj(String codtipotarj) {
        this.codtipotarj = codtipotarj == null ? null : codtipotarj.trim();
    }

	@Override
	protected void initNuevoBean() {}
}