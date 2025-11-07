package com.comerzzia.api.loyalty.persistence.cardsTypes.activities;

import java.io.Serializable;

public class TipoActTarjetaKey implements Serializable {

	private static final long serialVersionUID = -2780968459567788418L;

	private String uidActividad;

    private String codtipotarj;

    public String getUidActividad() {
        return uidActividad;
    }

    public void setUidActividad(String uidActividad) {
        this.uidActividad = uidActividad == null ? null : uidActividad.trim();
    }

    public String getCodtipotarj() {
        return codtipotarj;
    }

    public void setCodtipotarj(String codtipotarj) {
        this.codtipotarj = codtipotarj == null ? null : codtipotarj.trim();
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((codtipotarj == null) ? 0 : codtipotarj.hashCode());
		result = prime * result
				+ ((uidActividad == null) ? 0 : uidActividad.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof TipoActTarjetaKey))
			return false;
		TipoActTarjetaKey other = (TipoActTarjetaKey) obj;
		if (codtipotarj == null) {
			if (other.codtipotarj != null)
				return false;
		} else if (!codtipotarj.equals(other.codtipotarj))
			return false;
		if (uidActividad == null) {
			if (other.uidActividad != null)
				return false;
		} else if (!uidActividad.equals(other.uidActividad))
			return false;
		return true;
	}
}