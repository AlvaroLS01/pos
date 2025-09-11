package com.comerzzia.iskaypet.pos.persistence.ticket.contrato.trazabilidad.detalles;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "extension")
public class DetallesTrazabilidadDto {
	@XmlElement(name = "fldValue")
	private String identificacionTrazabilidad;
	// Es un valor fijo que requiere s4
	@XmlElement(name = "fldGrp")
	private String fldGrp = "IDENT";
	@XmlElement(name = "fldName")
	private String fldName;

	//Este atributo lo incluimos para saber si el dato es seleccionado de bbdd o es insertado manualmente, en este caso, es editable
	private boolean isEditable;

	public String getIdentificacionTrazabilidad() {
		return identificacionTrazabilidad;
	}

	public void setIdentificacionTrazabilidad(String identificacionTrazabilidad) {
		this.identificacionTrazabilidad = identificacionTrazabilidad;
	}

	public String getFldName() {
		return fldName;
	}

	public void setFldName(String fldName) {
		this.fldName = fldName;
	}

	public String getFldGrp() {
		return fldGrp;
	}

	public void setFldGrp(String fldGrp) {
		this.fldGrp = fldGrp;
	}

	public boolean isEditable() {
		return isEditable;
	}

	public void setEditable(boolean isEditable) {
		this.isEditable = isEditable;
	}

}
