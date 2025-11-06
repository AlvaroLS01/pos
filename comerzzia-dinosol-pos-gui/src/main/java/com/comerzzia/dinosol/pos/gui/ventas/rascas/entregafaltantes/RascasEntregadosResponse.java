package com.comerzzia.dinosol.pos.gui.ventas.rascas.entregafaltantes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "rascas_entregados")
@XmlAccessorType(XmlAccessType.FIELD)
public class RascasEntregadosResponse {

	@XmlElement(name = "rascas_entregados")
	private int rascasEntregados;

	@XmlElement(name = "rascas_concedidos")
	private int rascasConcedidos;

	@XmlElement(name = "ultima_linea")
	private int ultimaLinea;

	public int getRascasEntregados() {
		return rascasEntregados;
	}

	public void setRascasEntregados(int rascasEntregados) {
		this.rascasEntregados = rascasEntregados;
	}

	public int getRascasConcedidos() {
		return rascasConcedidos;
	}

	public void setRascasConcedidos(int rascasConcedidos) {
		this.rascasConcedidos = rascasConcedidos;
	}

	public int getUltimaLinea() {
		return ultimaLinea;
	}

	public void setUltimaLinea(int ultimaLinea) {
		this.ultimaLinea = ultimaLinea;
	}

}
