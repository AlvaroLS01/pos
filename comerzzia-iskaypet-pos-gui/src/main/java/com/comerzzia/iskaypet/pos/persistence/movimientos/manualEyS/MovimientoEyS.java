package com.comerzzia.iskaypet.pos.persistence.movimientos.manualEyS;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
@XmlAccessorType(XmlAccessType.FIELD)
public class MovimientoEyS extends MovimientoEySKey {

	@XmlElement(name = "tipoAuditoria")
	private String tipoAuditoria;
	@XmlElement(name = "codMotivo")
	private Long codMotivo;
	@XmlElement(name = "desMotivo")
	private String desMotivo;
	@XmlElement(name = "observaciones")
	private String observaciones;

	public String getTipoAuditoria() {
		return tipoAuditoria;
	}

	public void setTipoAuditoria(String tipoAuditoria) {
		this.tipoAuditoria = tipoAuditoria == null ? null : tipoAuditoria.trim();
	}

	public Long getCodMotivo() {
		return codMotivo;
	}

	public void setCodMotivo(Long codMotivo) {
		this.codMotivo = codMotivo;
	}

	public String getDesMotivo() {
		return desMotivo;
	}

	public void setDesMotivo(String desMotivo) {
		this.desMotivo = desMotivo == null ? null : desMotivo.trim();
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones == null ? null : observaciones.trim();
	}
}