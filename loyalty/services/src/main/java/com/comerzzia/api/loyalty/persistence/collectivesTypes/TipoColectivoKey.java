package com.comerzzia.api.loyalty.persistence.collectivesTypes;

import com.comerzzia.core.util.base.MantenimientoBean;

public class TipoColectivoKey extends MantenimientoBean {

	private static final long serialVersionUID = -796144831432072835L;

	private String uidInstancia;

    private String codtipcolectivo;

    public TipoColectivoKey() {}
    
    public TipoColectivoKey(String uidInstancia, String codtipcolectivo) {
    	this.uidInstancia = uidInstancia;
    	this.codtipcolectivo = codtipcolectivo;
    }
    
    public String getUidInstancia() {
        return uidInstancia;
    }

    public void setUidInstancia(String uidInstancia) {
        this.uidInstancia = uidInstancia == null ? null : uidInstancia.trim();
    }

    public String getCodtipcolectivo() {
        return codtipcolectivo;
    }

    public void setCodtipcolectivo(String codtipcolectivo) {
        this.codtipcolectivo = codtipcolectivo == null ? null : codtipcolectivo.trim();
    }

	@Override
	protected void initNuevoBean() {}
}