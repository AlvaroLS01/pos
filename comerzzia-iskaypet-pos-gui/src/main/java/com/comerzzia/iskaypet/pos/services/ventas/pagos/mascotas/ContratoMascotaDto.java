package com.comerzzia.iskaypet.pos.services.ventas.pagos.mascotas;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "contrato_animal")
public class ContratoMascotaDto {

	@XmlElement(name = "uidActividad")
	private String uidActividad;

	@XmlElement(name = "unique_id_evicertia")
	private String uniqueId;

	@XmlElement(name = "uidTicket")
	private String uidTicket;

	@XmlElement(name = "fecha_envio")
	private Date fechaEnvio;

	@XmlElement(name = "contrato")
	private String contrato;

	public String getUidActividad() {
		return uidActividad;
	}

	public void setUidActividad(String uidActividad) {
		this.uidActividad = uidActividad;
	}

	public String getContrato() {
		return contrato;
	}

	public void setContrato(String contrato) {
		this.contrato = contrato;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public String getUidTicket() {
		return uidTicket;
	}

	public void setUidTicket(String uidTicket) {
		this.uidTicket = uidTicket;
	}

	public Date getFechaEnvio() {
		return fechaEnvio;
	}

	public void setFechaEnvio(Date fechaEnvio) {
		this.fechaEnvio = fechaEnvio;
	}

}
