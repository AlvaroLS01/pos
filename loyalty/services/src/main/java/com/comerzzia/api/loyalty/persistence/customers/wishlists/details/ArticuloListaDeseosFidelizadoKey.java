package com.comerzzia.api.loyalty.persistence.customers.wishlists.details;

import com.comerzzia.core.util.base.MantenimientoBean;

public class ArticuloListaDeseosFidelizadoKey extends MantenimientoBean {
    
	private static final long serialVersionUID = 7238323397108705256L;

	private String uidActividad;

    private String uidListaDeseos;

    private Integer linea;

    private String codart;

    private String desglose1;

    private String desglose2;

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

    public Integer getLinea() {
        return linea;
    }

    public void setLinea(Integer linea) {
        this.linea = linea;
    }

    public String getCodart() {
        return codart;
    }

    public void setCodart(String codart) {
        this.codart = codart == null ? null : codart.trim();
    }

    public String getDesglose1() {
        return desglose1;
    }

    public void setDesglose1(String desglose1) {
        this.desglose1 = desglose1 == null ? null : desglose1.trim();
    }

    public String getDesglose2() {
        return desglose2;
    }

    public void setDesglose2(String desglose2) {
        this.desglose2 = desglose2 == null ? null : desglose2.trim();
    }

	@Override
	protected void initNuevoBean() {
		
	}
	//INICIO MÉTODOS PERSONALIZADOS--------------------------------------------
    
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ArticuloListaDeseosFidelizadoKey))
			return false;
		ArticuloListaDeseosFidelizadoKey other = (ArticuloListaDeseosFidelizadoKey) obj;
		if (uidActividad == null) {
			if (other.uidActividad != null)
				return false;
		}else if (uidListaDeseos == null) {
			if (other.uidListaDeseos != null)
				return false;
		}else if (linea == null) {
			if (other.linea != null)
				return false;
		}else if (codart == null) {
			if (other.codart != null)
				return false;
		}else if (desglose1 == null) {
			if (other.desglose1 != null)
				return false;
		}
		else if (desglose2 == null) {
			if (other.desglose2 != null)
				return false;
		}else if (!uidActividad.equals(other.uidActividad))
			return false; 
		else if (!uidListaDeseos.equals(other.uidListaDeseos))
			return false;
		else if (!linea.equals(other.linea))
			return false;
		else if (!codart.equals(other.codart))
			return false;
		else if (!desglose1.equals(other.desglose1))
			return false;
		else if (!desglose2.equals(other.desglose2))
			return false;
		return true;
	}
	//FIN MÉTODOS PERSONALIZADOS-----------------------------------------------
}