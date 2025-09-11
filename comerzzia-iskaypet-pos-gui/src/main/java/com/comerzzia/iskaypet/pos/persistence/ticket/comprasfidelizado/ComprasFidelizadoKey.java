package com.comerzzia.iskaypet.pos.persistence.ticket.comprasfidelizado;

import com.comerzzia.core.util.base.MantenimientoBean;

@SuppressWarnings("serial")
public class ComprasFidelizadoKey extends MantenimientoBean{
    private String uidActividad;

    private Long idClieAlbaran;

    public String getUidActividad() {
        return uidActividad;
    }

    public void setUidActividad(String uidActividad) {
        this.uidActividad = uidActividad == null ? null : uidActividad.trim();
    }

    public Long getIdClieAlbaran() {
        return idClieAlbaran;
    }

    public void setIdClieAlbaran(Long idClieAlbaran) {
        this.idClieAlbaran = idClieAlbaran;
    }

	@Override
	protected void initNuevoBean() {
	}
}