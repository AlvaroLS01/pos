package com.comerzzia.bimbaylola.pos.services.edicom.fieldvalues;

public class Cabcustom {

	public static final String ETIQUETA_SISTEMA_COMERZZIA = "Sistema";
	public static final String ETIQUETA_COMERZZIA = "COMERZZIA";
	public static final String ETIQUETA_REFERENCIA_INTERNA = "REFERENCIA_INTERNA";
	
	
	private static final String LABEL_CABCUSTOM = "CABCUSTOM";
	private String etiqueta;
	private String descripcion;
	private String valor; //TODO: -1????
	
	/**
	 * Solo habría que setear el valor
	 */
	public Cabcustom() {
	}
	
	public String getEtiqueta() {
		return etiqueta;
	}
	
	public String getDescripcion() {
		return descripcion;
	}
	
	public String getValor() {
		return valor;
	}
	
	public void setEtiqueta(String etiqueta) {
		this.etiqueta = etiqueta;
	}
	
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	public void setValor(String valor) {
		this.valor = valor;
	}

	@Override
	public String toString() {
		return LABEL_CABCUSTOM + "|" + getEtiqueta() + "|" + getDescripcion()+ "|" + getValor() + "|";
	}
	
	
	
}
