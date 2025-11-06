package com.comerzzia.dinosol.pos.persistence.restricciones;

import java.math.BigDecimal;

/**
 * Clase que almacena las restricciones para un documento (FS). Se carga desde BD (D_TIPOS_DOCUMENTOS_PROP_TBL).
 */
public class RestriccionDocumentoBean {

	private BigDecimal cantidadMaxima;
	private BigDecimal importeMaximo;

	public BigDecimal getCantidadMaxima() {
		return cantidadMaxima;
	}

	public void setCantidadMaxima(BigDecimal cantidadMaxima) {
		this.cantidadMaxima = cantidadMaxima;
	}

	public BigDecimal getImporteMaximo() {
		return importeMaximo;
	}

	public void setImporteMaximo(BigDecimal importeMaximo) {
		this.importeMaximo = importeMaximo;
	}

}
