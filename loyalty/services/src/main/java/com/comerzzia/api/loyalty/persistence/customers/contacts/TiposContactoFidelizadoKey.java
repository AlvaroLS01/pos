package com.comerzzia.api.loyalty.persistence.customers.contacts;

import com.comerzzia.core.util.base.MantenimientoBean;

public class TiposContactoFidelizadoKey extends MantenimientoBean {

	private static final long serialVersionUID = -7649979328844290091L;

	private String uidInstancia;

    private Long idFidelizado;

    protected String codTipoCon;

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

    public String getCodTipoCon() {
        return codTipoCon;
    }

    public void setCodTipoCon(String codTipoCon) {
        this.codTipoCon = codTipoCon == null ? null : codTipoCon.trim();
    }

	@Override
	protected void initNuevoBean() {
	}
	
	@Override
    public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((codTipoCon == null) ? 0 : codTipoCon.hashCode());
	    return result;
    }

	@Override
    public boolean equals(Object obj) {
	    if (this == obj)
		    return true;
	    if (obj == null)
		    return false;
	    if (getClass() != obj.getClass())
		    return false;
	    TiposContactoFidelizadoBean other = (TiposContactoFidelizadoBean) obj;
	    if (codTipoCon == null) {
		    if (other.codTipoCon != null)
			    return false;
	    }
	    else if (!codTipoCon.equals(other.codTipoCon))
		    return false;
	    return true;
    }
}