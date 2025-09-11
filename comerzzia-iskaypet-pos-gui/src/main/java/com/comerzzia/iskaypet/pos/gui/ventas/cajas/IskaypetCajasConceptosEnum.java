package com.comerzzia.iskaypet.pos.gui.ventas.cajas;

public enum IskaypetCajasConceptosEnum {

	APERTURA_CAJON("00", "ABRIR CAJON", true),
	AVISO_RETIRADA_EFECTIVO("01", "AVISO_RETIRADA_EFECTIVO", false),
	RETIRADA_EFECTIVO("01", "RETIRADA EFECTIVO", true),

	SALIDA_EFECTIVO("02", "SALIDA CAJA", true),
	ENTRADA_EFECTIVO("03", "ENTRADA CAJA", true),
	;

	private String codConcepto;
	private String desConcepto;
	private Boolean requiereGerente;

	IskaypetCajasConceptosEnum(String codConcepto, String desConcepto, Boolean requiereGerente) {
		this.codConcepto = codConcepto;
		this.desConcepto = desConcepto;
		this.requiereGerente = requiereGerente;
	}

	public String getCodConcepto() {
		return codConcepto;
	}

	public String getDesConcepto() {
		return desConcepto;
	}

	public Boolean getRequiereGerente() {
		return requiereGerente;
	}
}
