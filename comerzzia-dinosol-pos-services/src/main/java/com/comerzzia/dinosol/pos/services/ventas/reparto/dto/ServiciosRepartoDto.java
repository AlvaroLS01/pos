package com.comerzzia.dinosol.pos.services.ventas.reparto.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "servicios_reparto")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiciosRepartoDto {

	@XmlElement(name = "servicio_reparto")
	private List<ServicioRepartoDto> servicios;

	public List<ServicioRepartoDto> getServicios() {
		return servicios;
	}

	public void setServicios(List<ServicioRepartoDto> servicios) {
		this.servicios = servicios;
	}

}
