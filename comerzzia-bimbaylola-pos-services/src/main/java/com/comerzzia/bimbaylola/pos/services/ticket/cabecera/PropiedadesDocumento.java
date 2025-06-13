package com.comerzzia.bimbaylola.pos.services.ticket.cabecera;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class PropiedadesDocumento {

	@XmlElement(name = "tipo_documento")
	private String tipoDocumento;

	@XmlElement(name = "signo_documento")
	private String signoDocumento;

	@XmlElement(name = "tipoTransaccion")
	private String tipoTransaccion;

	public PropiedadesDocumento() {
	}

	public PropiedadesDocumento(String tipoDocumento, String signoDocumento, String tipoTransaccion) {
		super();
		this.tipoDocumento = tipoDocumento;
		this.signoDocumento = signoDocumento;
		this.tipoTransaccion = tipoTransaccion;
	}

	public String getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(String tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public String getSignoDocumento() {
		return signoDocumento;
	}

	public void setSignoDocumento(String signoDocumento) {
		this.signoDocumento = signoDocumento;
	}

	public String getTipoTransaccion() {
		return tipoTransaccion;
	}

	public void setTipoTransaccion(String tipoTransaccion) {
		this.tipoTransaccion = tipoTransaccion;
	}

}
