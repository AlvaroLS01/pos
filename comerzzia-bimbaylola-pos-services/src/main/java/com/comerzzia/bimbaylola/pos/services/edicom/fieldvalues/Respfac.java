package com.comerzzia.bimbaylola.pos.services.edicom.fieldvalues;

import com.comerzzia.bimbaylola.pos.services.edicom.util.EdicomFormat;

public class Respfac {

	/*
	 * En el toString se genera dos veces
	 */
	public static final String LABEL_RESP = "RESPFAC";
	public static final String TIPO_INTERLOCUTOR_EMISOR = "E";
	public static final String TIPO_INTERLOCUTOR_RECEPTOR = "R";
	public static final String CODIGO_CONSUMIDOR_FINAL = "R-99-PN";
	public static final String CODIGO_CLIENTE_IDENTIFICADO = "O-48";
	public String codigo;
	/*
	 * VALORES FIJOS VACÍOS
	 */
	public String tipoInterlocutor;
	public String listaCodigo;
	public String nombreCodigo;

	/**
	 * Solo hay que setear codigo, si es consumidor final = CODIGO_CONSUMIDOR_FINAL
	 * si es cliente identificado = CODIGO_CLIENTE_IDENTIFICADO
	 */
	public Respfac() {

	}
	
	public String getTipoInterlocutor() {
		return tipoInterlocutor;
	}

	public String getCodigo() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(codigo, 50);
	}

	public String getListaCodigo() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(listaCodigo, 50);
	}
	
	public String getNombreCodigo() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(nombreCodigo, 255);
	}

	
	public void setTipoInterlocutor(String tipoInterlocutor) {
		this.tipoInterlocutor = tipoInterlocutor;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public void setListaCodigo(String listaCodigo) {
		this.listaCodigo = listaCodigo;
	}

	public void setNombreCodigo(String nombreCodigo) {
		this.nombreCodigo = nombreCodigo;
	}
	

	@Override
	public String toString() {
		return LABEL_RESP + "|" + getTipoInterlocutor() + "|" + getCodigo() + "|" + getListaCodigo() + "|" + getNombreCodigo() +"|";
	}



}
