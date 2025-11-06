package com.comerzzia.dinosol.pos.services.tarjetasregalo;

import java.math.BigDecimal;

public class TipoTarjetaRegaloDto {

	private String codArticulo;

	private String prefijo;

	private BigDecimal importeMinimo;

	private BigDecimal importeMaximo;

	private Integer diasVigencia;

	public String getCodArticulo() {
		return codArticulo;
	}

	public void setCodArticulo(String codArticulo) {
		this.codArticulo = codArticulo;
	}

	public String getPrefijo() {
		return prefijo;
	}

	public void setPrefijo(String prefijo) {
		this.prefijo = prefijo;
	}

	public BigDecimal getImporteMinimo() {
		return importeMinimo;
	}

	public void setImporteMinimo(BigDecimal importeMinimo) {
		this.importeMinimo = importeMinimo;
	}

	public BigDecimal getImporteMaximo() {
		return importeMaximo;
	}

	public void setImporteMaximo(BigDecimal importeMaximo) {
		this.importeMaximo = importeMaximo;
	}

	public Integer getDiasVigencia() {
		return diasVigencia;
	}

	public void setDiasVigencia(Integer diasVigencia) {
		this.diasVigencia = diasVigencia;
	}

}
