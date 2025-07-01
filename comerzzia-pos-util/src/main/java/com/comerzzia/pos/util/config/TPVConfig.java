package com.comerzzia.pos.util.config;

import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;
import lombok.NoArgsConstructor;

@XmlRootElement(name = "Parametros")
@Data
@NoArgsConstructor
public class TPVConfig {
	private Tpv tpv;
}
