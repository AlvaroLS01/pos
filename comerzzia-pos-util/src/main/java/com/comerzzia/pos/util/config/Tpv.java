package com.comerzzia.pos.util.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class Tpv {
	@XmlElement(name = "uid_actividad")
	private String uidActividad;
	@XmlElement(name = "uid_caja")
	private String uidCaja;
}
