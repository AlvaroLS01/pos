package com.comerzzia.dinosol.pos.services.promociones.tipos.especificos;

public class DinoArticuloLineaDTO {
	
	protected String codArticulo;
	protected String desglose1;
	protected String desglose2;
	
	public DinoArticuloLineaDTO(String codArticulo, String desglose1, String desglose2) {
		this.codArticulo = codArticulo;
		this.desglose1 = desglose1;
		this.desglose2 = desglose2;
	}
	
	public String getCodArticulo() {
		return codArticulo;
	}
	public void setCodArticulo(String codArticulo) {
		this.codArticulo = codArticulo;
	}
	public String getDesglose1() {
		return desglose1;
	}
	public void setDesglose1(String desglose1) {
		this.desglose1 = desglose1;
	}
	public String getDesglose2() {
		return desglose2;
	}
	public void setDesglose2(String desglose2) {
		this.desglose2 = desglose2;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codArticulo == null) ? 0 : codArticulo.hashCode());
		result = prime * result + ((desglose1 == null) ? 0 : desglose1.hashCode());
		result = prime * result + ((desglose2 == null) ? 0 : desglose2.hashCode());
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
		DinoArticuloLineaDTO other = (DinoArticuloLineaDTO) obj;
		if (codArticulo == null) {
			if (other.codArticulo != null)
				return false;
		} else if (!codArticulo.equals(other.codArticulo))
			return false;
		if (desglose1 == null) {
			if (other.desglose1 != null)
				return false;
		} else if (!desglose1.equals(other.desglose1))
			return false;
		if (desglose2 == null) {
			if (other.desglose2 != null)
				return false;
		} else if (!desglose2.equals(other.desglose2))
			return false;
		return true;
	}

}
