package com.comerzzia.dinosol.pos.services.ticket.cabecera;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.comerzzia.pos.util.xml.MapAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
public class TarjetaIdentificacionDto {

	private String numeroTarjeta;

	private String tipoTarjeta;

	private boolean procesamientoOffline;

	private Object respuesta;

	@XmlElementWrapper
	@XmlElement(name = "linea_impresion")
	private List<String> lineasImpresion;

	@XmlJavaTypeAdapter(MapAdapter.class)
	private Map<String, Object> adicionales;

	public String getNumeroTarjeta() {
		return numeroTarjeta;
	}

	public void setNumeroTarjeta(String numeroTarjeta) {
		this.numeroTarjeta = numeroTarjeta;
	}

	public String getTipoTarjeta() {
		return tipoTarjeta;
	}

	public void setTipoTarjeta(String tipoTarjeta) {
		this.tipoTarjeta = tipoTarjeta;
	}

	public Object getRespuesta() {
		return respuesta;
	}

	public void setRespuesta(Object respuesta) {
		this.respuesta = respuesta;
	}

	public boolean isProcesamientoOffline() {
		return procesamientoOffline;
	}

	public void setProcesamientoOffline(boolean procesamientoOffline) {
		this.procesamientoOffline = procesamientoOffline;
	}

	public List<String> getLineasImpresion() {
		return lineasImpresion;
	}

	public void setLineasImpresion(List<String> lineasImpresion) {
		this.lineasImpresion = lineasImpresion;
	}

	public Map<String, Object> getAdicionales() {
		return adicionales;
	}

	public void setAdicionales(Map<String, Object> adicionales) {
		this.adicionales = adicionales;
	}

	public void putAdicional(String key, Object value) {
		if (this.adicionales == null) {
			this.adicionales = new HashMap<String, Object>();
		}

		this.adicionales.put(key, value);
	}

}
