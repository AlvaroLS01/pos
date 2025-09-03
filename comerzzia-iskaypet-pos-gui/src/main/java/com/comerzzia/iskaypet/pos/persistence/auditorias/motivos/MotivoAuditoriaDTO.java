package com.comerzzia.iskaypet.pos.persistence.auditorias.motivos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Primary
@Component
@Scope("prototype")
@XmlAccessorType(XmlAccessType.FIELD)
public class MotivoAuditoriaDTO {

	@XmlElement(name = "codigo")
	private Long codigo;

	@XmlElement(name = "descripcion")
	private String descripcion;

	@XmlElement(name = "codEmp")
	private String codEmp;

	@XmlElement(name = "motivoActivo")
	private boolean motivoActivo;

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcionDevolucion) {
		this.descripcion = descripcionDevolucion;
	}

	public String getCodEmp() {
		return codEmp;
	}

	public void setCodEmp(String codEmp) {
		this.codEmp = codEmp;
	}

	public boolean getMotivoActivo() {
		return motivoActivo;
	}

	public void setMotivoActivo(boolean motivoActivo) {
		this.motivoActivo = motivoActivo;
	}

}
