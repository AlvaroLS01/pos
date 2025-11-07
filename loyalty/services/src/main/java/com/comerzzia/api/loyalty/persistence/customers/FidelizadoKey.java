package com.comerzzia.api.loyalty.persistence.customers;

import javax.xml.bind.annotation.XmlType;

import com.comerzzia.core.util.base.MantenimientoBean;

@XmlType(name="fidKey")
public class FidelizadoKey extends MantenimientoBean {

	private static final long serialVersionUID = 3581408430351843550L;

	private String uidInstancia;

    private Long idFidelizado;

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

	@Override
	protected void initNuevoBean() {
	}
}