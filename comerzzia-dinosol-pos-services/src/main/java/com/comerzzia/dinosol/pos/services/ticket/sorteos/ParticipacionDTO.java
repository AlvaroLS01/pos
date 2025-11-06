package com.comerzzia.dinosol.pos.services.ticket.sorteos;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ParticipacionDTO {

	@XmlElement(name = "idparticipacion")
	private String codParticipacion;

	@XmlElement(name = "premio")
	private String idPremioAsociado;

	@XmlElement(name = "tipo_premio")
	private String codTipoPremio;

	@XmlElement(name = "codigoCupon")
	private String codCupon;

	@XmlTransient
	private String descripcionCupon;

	@XmlTransient
	private Date fechaFinCupon;

	public String getCodParticipacion() {
		return codParticipacion;
	}

	public void setCodParticipacion(String codParticipacion) {
		this.codParticipacion = codParticipacion;
	}

	public String getIdPremioAsociado() {
		return idPremioAsociado;
	}

	public void setIdPremioAsociado(String idPremioAsociado) {
		this.idPremioAsociado = idPremioAsociado;
	}

	public String getCodTipoPremio() {
		return codTipoPremio;
	}

	public void setCodTipoPremio(String codTipoPremio) {
		this.codTipoPremio = codTipoPremio;
	}

	public String getCodCupon() {
		return codCupon;
	}

	public void setCodCupon(String codCupon) {
		this.codCupon = codCupon;
	}

	public String getDescripcionCupon() {
		return descripcionCupon;
	}

	public void setDescripcionCupon(String descripcionCupon) {
		this.descripcionCupon = descripcionCupon;
	}

	public Date getFechaFinCupon() {
		return fechaFinCupon;
	}

	public void setFechaFinCupon(Date fechaFinCupon) {
		this.fechaFinCupon = fechaFinCupon;
	}

}
