package com.comerzzia.iskaypet.pos.persistence.articulos.anexos;

public class DetalleAnexoArticulo extends DetalleAnexoArticuloKey {

	private String tipoMaterial;
	private String valorCaracteristica;

	public String getTipoMaterial() {
		return tipoMaterial;
	}

	public void setTipoMaterial(String tipoMaterial) {
		this.tipoMaterial = tipoMaterial == null ? null : tipoMaterial.trim();
	}

	public String getValorCaracteristica() {
		return valorCaracteristica;
	}

	public void setValorCaracteristica(String valorCaracteristica) {
		this.valorCaracteristica = valorCaracteristica == null ? null : valorCaracteristica.trim();
	}

}
