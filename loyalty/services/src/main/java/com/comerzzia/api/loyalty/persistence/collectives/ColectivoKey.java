package com.comerzzia.api.loyalty.persistence.collectives;

import com.comerzzia.core.model.i18n.InternacionalizableBean;

public class ColectivoKey extends InternacionalizableBean {

	private static final long serialVersionUID = 644572199108139699L;

	private String uidInstancia;

    private String codColectivo;

    public ColectivoKey() {}
    
    public ColectivoKey(String uidInstancia, String codColectivo) {
    	this.uidInstancia = uidInstancia;
    	this.codColectivo = codColectivo;
    }
    
    public String getUidInstancia() {
        return uidInstancia;
    }

    public void setUidInstancia(String uidInstancia) {
        this.uidInstancia = uidInstancia == null ? null : uidInstancia.trim();
    }

    public String getCodColectivo() {
        return codColectivo;
    }

    public void setCodColectivo(String codColectivo) {
        this.codColectivo = codColectivo == null ? null : codColectivo.trim();
    }

	@Override
	protected void initNuevoBean() {}
}