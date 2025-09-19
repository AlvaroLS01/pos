package com.comerzzia.bimbaylola.pos.services.taxFree;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "xml_voucher")
@Component
@Primary
@Scope("prototype")
public class TaxFreeXMLVoucher {

	@XmlElement(name = "numero_formulario")
	private String numeroFormulario;

	@XmlElement(name = "uid_ticket")
	private String uidTicket;

	@XmlElement(name = "uid_actividad")
	private String uidActividad;

	@XmlElement(name = "fecha")
	private String fecha;

	public String getNumeroFormulario() {
		return numeroFormulario;
	}

	public void setNumeroFormulario(String numeroFormulario) {
		this.numeroFormulario = numeroFormulario;
	}

	public String getUidTicket() {
		return uidTicket;
	}

	public void setUidTicket(String uidTicket) {
		this.uidTicket = uidTicket;
	}

	public String getUidActividad() {
		return uidActividad;
	}

	public void setUidActividad(String uidActividad) {
		this.uidActividad = uidActividad;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

}
