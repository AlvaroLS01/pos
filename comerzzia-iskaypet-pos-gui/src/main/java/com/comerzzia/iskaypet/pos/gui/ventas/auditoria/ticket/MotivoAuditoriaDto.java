package com.comerzzia.iskaypet.pos.gui.ventas.auditoria.ticket;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "motivoAuditoria")
public class MotivoAuditoriaDto {
	
	@XmlElement(name = "codigo")
	private Integer	codigo;

	@XmlElement(name = "descripcion")
	private String descripcion;
	
	@XmlElement(name = "permiteObservaciones")
	private String permiteObservaciones;

	public Integer getCodigo() {
		return codigo;
	}
	
	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	public String getPermiteObservaciones() {
		return permiteObservaciones;
	}

	public void setPermiteObservaciones(String permiteObservaciones) {
		this.permiteObservaciones = permiteObservaciones;
	}
	
}
