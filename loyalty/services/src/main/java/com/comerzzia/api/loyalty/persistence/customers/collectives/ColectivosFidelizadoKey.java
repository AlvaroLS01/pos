package com.comerzzia.api.loyalty.persistence.customers.collectives;

import com.comerzzia.core.util.base.MantenimientoBean;

public class ColectivosFidelizadoKey extends MantenimientoBean {

	private static final long serialVersionUID = 280635939929217577L;

	private String uidInstancia;

    private Long idFidelizado;

    private String codColectivo;

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

    public String getCodColectivo() {
        return codColectivo;
    }

    public void setCodColectivo(String codColectivo) {
        this.codColectivo = codColectivo == null ? null : codColectivo.trim();
    }

	@Override
	protected void initNuevoBean() {
	}
}