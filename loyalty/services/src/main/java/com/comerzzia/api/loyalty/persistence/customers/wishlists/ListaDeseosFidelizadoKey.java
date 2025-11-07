package com.comerzzia.api.loyalty.persistence.customers.wishlists;

import com.comerzzia.core.util.base.MantenimientoBean;

public class ListaDeseosFidelizadoKey extends MantenimientoBean {
   
	private static final long serialVersionUID = 9134116002330940176L;

	private String uidActividad;

    private String uidListaDeseos;

    public String getUidActividad() {
        return uidActividad;
    }

    public void setUidActividad(String uidActividad) {
        this.uidActividad = uidActividad == null ? null : uidActividad.trim();
    }

    public String getUidListaDeseos() {
        return uidListaDeseos;
    }

    public void setUidListaDeseos(String uidListaDeseos) {
        this.uidListaDeseos = uidListaDeseos == null ? null : uidListaDeseos.trim();
    }

	@Override
	protected void initNuevoBean() {
		
	}
}