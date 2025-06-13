package com.comerzzia.bimbaylola.pos.services.edicom.fieldvalues;

import java.math.BigDecimal;

import com.comerzzia.bimbaylola.pos.services.edicom.util.EdicomFormat;

public class Impfaclin {

	private final String LABEL_IMPLIN = "IMPFACLIN";
	/*
	 * RELLENABLES
	 */
	private BigDecimal montoImpuesto;
	private String indicadorImpRetenido; // valor fijo
	private BigDecimal baseImponible;
	private String porcentajeImpuesto; // 19%
	/*
	 * VALORES FIJOS
	 */
	private String tipoImpuesto; // valor fijo
	private String unidadBaseMedida;
	private String unidadBaseMedidaUOM;
	private BigDecimal importePorUnidad;
	private String descrImpuesto;
	private BigDecimal redondeoImpuesto;

	
	public Impfaclin() {
		tipoImpuesto = "01";
		indicadorImpRetenido = "false";
	}
	
	public String getMontoImpuesto() {
		return EdicomFormat.rellenaCerosNumericos(montoImpuesto, 15, 6);
	}

	public String getIndicadorImpRetenido() {
		return indicadorImpRetenido;
	}

	public String getBaseImponible() {
		return EdicomFormat.rellenaCerosNumericos(baseImponible, 15, 6);
	}

	public String getPorcentajeImpuesto() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(porcentajeImpuesto, 15);
	}

	public String getTipoImpuesto() {
		return tipoImpuesto;
	}

	public String getUnidadBaseMedida() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(unidadBaseMedida, 15);
	}

	public String getUnidadBaseMedidaUOM() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(unidadBaseMedidaUOM, 15);
	}

	public String getImportePorUnidad() {
		return EdicomFormat.rellenaCerosNumericos(importePorUnidad, 20, 6);
	}

	public String getDescrImpuesto() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(descrImpuesto, 255);
	}

	public String getRedondeoImpuesto() {
		return EdicomFormat.rellenaCerosNumericos(redondeoImpuesto, 15, 6);
	}

	public void setMontoImpuesto(BigDecimal montoImpuesto) {
		this.montoImpuesto = montoImpuesto;
	}

	public void setIndicadorImpRetenido(String indicadorImpRetenido) {
		this.indicadorImpRetenido = indicadorImpRetenido;
	}

	public void setBaseImponible(BigDecimal baseImponible) {
		this.baseImponible = baseImponible;
	}

	public void setPorcentajeImpuesto(String porcentajeImpuesto) {
		this.porcentajeImpuesto = porcentajeImpuesto;
	}

	public void setTipoImpuesto(String tipoImpuesto) {
		this.tipoImpuesto = tipoImpuesto;
	}

	public void setUnidadBaseMedida(String unidadBaseMedida) {
		this.unidadBaseMedida = unidadBaseMedida;
	}

	public void setUnidadBaseMedidaUOM(String unidadBaseMedidaUOM) {
		this.unidadBaseMedidaUOM = unidadBaseMedidaUOM;
	}

	public void setImportePorUnidad(BigDecimal importePorUnidad) {
		this.importePorUnidad = importePorUnidad;
	}

	public void setDescrImpuesto(String descrImpuesto) {
		this.descrImpuesto = descrImpuesto;
	}

	public void setRedondeoImpuesto(BigDecimal redondeoImpuesto) {
		this.redondeoImpuesto = redondeoImpuesto;
	}

	@Override
	public String toString() {
		return LABEL_IMPLIN + "|" + getMontoImpuesto() + "|" + getIndicadorImpRetenido() + "|" + getBaseImponible() + "|" + getPorcentajeImpuesto() + "|" + getTipoImpuesto() + "|"
		        + getUnidadBaseMedida() + "|" + getUnidadBaseMedidaUOM() + "|" + getImportePorUnidad() + "|" + getDescrImpuesto() + "|" + getRedondeoImpuesto() + "|";
	}

}
