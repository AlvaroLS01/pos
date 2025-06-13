package com.comerzzia.bimbaylola.pos.services.edicom.fieldvalues;

import com.comerzzia.bimbaylola.pos.services.edicom.util.EdicomFormat;

public class Reffac {
	/*
	 * OBLIGATORIO EN NOTA CRÉDITO
	 */
	
	private static final String LABEL_REF = "REFFAC";
	/*
	 * Rellenables
	 */
	private String idFactura;
	private String UUID;
	private String fechaFactura;
	/*
	 * Valores fijos
	 */
	private String codigoRespuesta;
	private String descNatu;
	private String tipoDoc;
	private String tipoDocNoTributario;
	private String horaFactura;
	
	
	public Reffac() {
		codigoRespuesta = "5";
		descNatu = "Devolución";
		tipoDoc = "F";
	}
	
	public String getIdFactura() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(idFactura, 255);
	}
	
	public String getUUID() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(UUID, 255);
	}
	
	public String getFechaFactura() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(fechaFactura, 255);
	}
	
	public String getCodigoRespuesta() {
		return codigoRespuesta;
	}
	
	public String getDescNatu() {
		return descNatu;
	}
	
	public String getTipoDoc() {
		return tipoDoc;
	}
	
	public String getTipoDocNoTributario() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(tipoDocNoTributario, 5);
	}
	
	public String getHoraFactura() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(horaFactura, 255);
	}
	
	public void setIdFactura(String idFactura) {
		this.idFactura = idFactura;
	}
	
	public void setUUID(String uUID) {
		UUID = uUID;
	}
	
	public void setFechaFactura(String fechaFactura) {
		this.fechaFactura = fechaFactura;
	}
	
	public void setCodigoRespuesta(String codigoRespuesta) {
		this.codigoRespuesta = codigoRespuesta;
	}
	
	public void setDescNatu(String descNatu) {
		this.descNatu = descNatu;
	}
	
	public void setTipoDoc(String tipoDoc) {
		this.tipoDoc = tipoDoc;
	}
	
	public void setTipoDocNoTributario(String tipoDocNoTributario) {
		this.tipoDocNoTributario = tipoDocNoTributario;
	}
	
	public void setHoraFactura(String horaFactura) {
		this.horaFactura = horaFactura;
	}

	@Override
	public String toString() {
		return LABEL_REF + "|" + getIdFactura() + "|" + getUUID() + "|" + getFechaFactura() + "|" + getCodigoRespuesta() + "|" + getDescNatu() + "|" + getTipoDoc() + "|" + getTipoDocNoTributario() + "|"
		        + getHoraFactura() + "|";
	}
	
	
}
