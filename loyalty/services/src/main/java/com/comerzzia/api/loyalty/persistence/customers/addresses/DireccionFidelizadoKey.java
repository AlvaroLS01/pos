package com.comerzzia.api.loyalty.persistence.customers.addresses;

import com.comerzzia.core.util.base.MantenimientoBean;

public class DireccionFidelizadoKey extends MantenimientoBean {

	private static final long serialVersionUID = 7067271912820686910L;

	private String uidInstancia;

    private Long idFidelizado;

    private String descripcionDireccion;

    public String getUidInstancia() {
        return uidInstancia;
    }

    public void setUidInstancia(String uidInstancia) {
        this.uidInstancia = uidInstancia == null ? null : uidInstancia.trim();
    }

    public Long getIdFidelizado() {
        return idFidelizado;
    }

    public void setIdFidelizado(Long idFidelizado) {
        this.idFidelizado = idFidelizado;
    }

    public String getDescripcionDireccion() {
        return descripcionDireccion;
    }

    public void setDescripcionDireccion(String descripcionDireccion) {
        this.descripcionDireccion = descripcionDireccion == null ? null : descripcionDireccion.trim();
    }

	@Override
	protected void initNuevoBean() {
	}
}