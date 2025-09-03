package com.comerzzia.iskaypet.pos.persistence.articulos.lotes;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.util.format.FormatUtil;

@Component
@Scope("prototype")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "lote")
public class LoteDTO {

	@XmlElement(name = "valor")
	protected String lote;
	
	@XmlElement(name = "batch_number_s4")
	protected String batchNumberS4;
	
	@XmlElement(name = "fecha_caducidad")
	protected Date fechaCaducidad;

	public String getLote() {
		return lote;
	}

	public void setLote(String lote) {
		this.lote = lote;
	}

	public String getBatchNumberS4() {
		return batchNumberS4;
	}

	public void setBatchNumberS4(String batchNumberS4) {
		this.batchNumberS4 = batchNumberS4;
	}

	public Date getFechaCaducidad() {
		return fechaCaducidad;
	}

	public void setFechaCaducidad(Date fechaCaducidad) {
		this.fechaCaducidad = fechaCaducidad;
	}
	
	
	/* **************** */
	
	public String getFechaCaducidadString() {

		Date fecha = getFechaCaducidad();
		if (fecha == null) {
			return "";
		}
		return FormatUtil.getInstance().formateaFechaCorta(fecha);
	}
	
	/**
	 * GAP12.1 Devuelve "identificador" de lote que tiene en cuenta lotes que se llaman igual con distinto numberS4.
	 * 
	 * Esto sirve para tratar lotes con mismo batch pero distinto batchNumberS4 o lotes introducidos manualmente
	 * que no tienen batchNumberS4. Evita nulls.
	 * @return
	 */
	public String getConcatLoteLoteS4() {
		return String.join("", lote, batchNumberS4);
	}
	
}
