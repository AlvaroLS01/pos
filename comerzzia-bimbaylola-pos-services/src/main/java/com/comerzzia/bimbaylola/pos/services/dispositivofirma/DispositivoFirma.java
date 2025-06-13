package com.comerzzia.bimbaylola.pos.services.dispositivofirma;

import java.util.HashMap;
import java.util.Map;

public class DispositivoFirma implements IFirma {

	protected String modelo;
	protected HashMap<String, ByLConfiguracionModelo> listaConfiguracion;
	protected ByLConfiguracionModelo configuracionActual;

	public DispositivoFirma(String modelo) {
		this.modelo = modelo;
	}

	@Override
	public String getModelo() {
		return modelo;
	}

	@Override
	public String getManejador() {
		return listaConfiguracion.get(modelo).getManejador();
	}

	@Override
	public void getMetodosConexion() {
	}

	@Override
	public String getModoConexion() {
		return null;
	}

	public HashMap<String, ByLConfiguracionModelo> getListaConfiguracion() {
		return listaConfiguracion;
	}

	public void setListaConfiguracion(HashMap<String, ByLConfiguracionModelo> listaConfiguracion) {
		this.listaConfiguracion = listaConfiguracion;
	}

	public ByLConfiguracionModelo getConfiguracionActual() {
		return configuracionActual;
	}

	public void setConfiguracionActual(ByLConfiguracionModelo configuracionActual) {
		this.configuracionActual = configuracionActual;
	}

	@Override
	public void setModelo(String modelo) {
	}

	@Override
	public void setModoConexion(String modoConexion) {
	}

	@Override
	public Map<String, Object> firmar() {
		return null;
	}

	@Override
	public void iniciarDispositivoFirma() {
	}
}
