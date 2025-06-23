package com.comerzzia.bimbaylola.pos.services.edicom.fieldvalues;

import java.math.BigDecimal;

import com.comerzzia.bimbaylola.pos.services.edicom.util.EdicomFormat;

public class Autorizado {

	/*
	 * SOLO PARA CLIENTE IDENTIFICADO
	 */
	private static final String LABEL_AUT = "AUTORIZADO";
	
	public static final String CEDULA_CIUDADANIA = "CC";
	public static final String CEDULA_EXTRANJERIA = "CE";
	public static final String NIT = "NIT";
	public static final String PASAPORTE = "PAS";
	
	public static final BigDecimal CODIGO_CEDULA_CIUDADANIA = new BigDecimal(13);
	public static final BigDecimal CODIGO_CEDULA_EXTRANJERIA = new BigDecimal(22);
	public static final BigDecimal CODIGO_NIT = new BigDecimal(31);
	public static final BigDecimal CODIGO_PASAPORTE = new BigDecimal(41);
	/*
	 * Si es NIT irá con el dv 999999999(-1)
	 */
	private String autorizadoId;
	/*
	 * Dígito de control para NIT, si lo tiene añadir último dígito despues de -
	 */
	private String autorizadoDv;
	/*
	 * Tipos admitidos 13: Cédula de ciudadanía 22: Cédula de extranjería 31: NIT 41: Pasaporte
	 */
	private String autorizadoTipoIdentificacion;
	private String autorizadoNombre;

	public String getAutorizadoId() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(autorizadoId, 15);
	}

	public String getAutorizadoDv() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(autorizadoDv, 1);
	}

	public String getAutorizadoTipoIdentificacion() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(autorizadoTipoIdentificacion, 15);
	}

	public String getAutorizadoNombre() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(autorizadoNombre, 75);
	}

	public void setAutorizadoId(String autorizadoId) {
		this.autorizadoId = autorizadoId;
	}

	public void setAutorizadoDv(String autorizadoDv) {
		this.autorizadoDv = autorizadoDv;
	}

	public void setAutorizadoTipoIdentificacion(String autorizadoTipoIdentificacion) {
		this.autorizadoTipoIdentificacion = autorizadoTipoIdentificacion;
	}

	public void setAutorizadoNombre(String autorizadoNombre) {
		this.autorizadoNombre = autorizadoNombre;
	}

	@Override
	public String toString() {
		return LABEL_AUT + "|" + getAutorizadoId() + "|" + getAutorizadoDv() + "|" + getAutorizadoTipoIdentificacion() + "|" + getAutorizadoNombre() + "|";
	}

}
