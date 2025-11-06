package com.comerzzia.dinosol.pos.devices.caja;

import java.util.List;

import org.apache.log4j.Logger;

import com.comerzzia.pos.core.dispositivos.configuracion.ConfiguracionDispositivo;
import com.comerzzia.pos.core.dispositivos.dispositivo.DispositivoException;
import com.comerzzia.pos.core.dispositivos.dispositivo.cajon.ICajon;

import javafx.stage.Stage;

public class CajonVirtual implements ICajon {
	
	private Logger log = Logger.getLogger(CajonVirtual.class);

	@Override
	public void conecta() throws DispositivoException {
	}

	@Override
	public void desconecta() throws DispositivoException {
	}

	@Override
	public ConfiguracionDispositivo getConfiguracion() {
		return null;
	}

	@Override
	public void setConfiguracion(ConfiguracionDispositivo config) throws DispositivoException {
		config.getModelo();
	}

	@Override
	public int getEstado() {
		return 0;
	}

	@Override
	public void setEstado(int estado) {
	}

	@Override
	public boolean isConfigurable() {
		return false;
	}

	@Override
	public void configurar(Stage stage) {
	}

	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public boolean isCanBeReset() {
		return false;
	}

	@Override
	public boolean reset() {
		return false;
	}

	@Override
	public List<String> getAvailabilityErrors() {
		return null;
	}

	@Override
	public void abrir() {
		log.info("----------------- CAJÓN ABIERTO -----------------");
	}

	@Override
	public boolean isOpen() {
		return false;
	}

}
