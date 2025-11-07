package com.comerzzia.api.loyalty.persistence.contactsTypes;

import com.comerzzia.core.model.i18n.InternacionalizableBean;

public class TipoContactoKey extends InternacionalizableBean {

	private static final long serialVersionUID = -7481957584752961534L;

	private String uidInstancia;

    private String codTipoCon;

    public String getUidInstancia() {
        return uidInstancia;
    }

    public void setUidInstancia(String uidInstancia) {
        this.uidInstancia = uidInstancia == null ? null : uidInstancia.trim();
    }

    public String getCodTipoCon() {
        return codTipoCon;
    }

    public void setCodTipoCon(String codTipoCon) {
        this.codTipoCon = codTipoCon == null ? null : codTipoCon.trim();
    }

	@Override
	protected void initNuevoBean() {
	}
}