package com.comerzzia.bimbaylola.pos.services.dispositivofirma;

import java.util.HashMap;
import java.util.Map;

public class FirmaNoConfig implements IFirma {

	protected String modelo;

	@Override
	public String getManejador() {
		return null;
	}

	@Override
	public void getMetodosConexion() {

	}

	@Override
	public HashMap<String, ByLConfiguracionModelo> getListaConfiguracion() {
		return null;
	}

	@Override
	public String getModelo() {
		return modelo;
	}

	@Override
	public String getModoConexion() {
		return null;
	}

	@Override
	public void setModelo(String modelo) {
		this.modelo = modelo;
	}

	@Override
	public void setModoConexion(String modoConexion) {
	}

	@Override
	public ByLConfiguracionModelo getConfiguracionActual() {
		return null;
	}

	@Override
	public void setConfiguracionActual(ByLConfiguracionModelo configuracionActual) {
	}

	@Override
	public Map<String, Object> firmar() {
		return null;
	}

	@Override
	public void iniciarDispositivoFirma() {

	}
}
