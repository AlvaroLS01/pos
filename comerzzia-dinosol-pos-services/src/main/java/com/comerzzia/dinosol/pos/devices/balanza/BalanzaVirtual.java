package com.comerzzia.dinosol.pos.devices.balanza;

import java.math.BigDecimal;
import java.util.List;

import com.comerzzia.pos.core.dispositivos.configuracion.ConfiguracionDispositivo;
import com.comerzzia.pos.core.dispositivos.dispositivo.DispositivoException;
import com.comerzzia.pos.core.dispositivos.dispositivo.balanza.IBalanza;

import gnu.io.SerialPort;
import javafx.stage.Stage;

public class BalanzaVirtual implements IBalanza {

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
	public void pedirPeso() {
	}

	@Override
	public Double getPeso(BigDecimal precio) {
		return Math.random() * 5.000;
	}

	@Override
	public String getTrama() {
		return null;
	}

	@Override
	public Integer getIntervaloPeticion() {
		return null;
	}

	@Override
	public void inicializarEventos(SerialPort serialPort) {
	}

	@Override
	public int getIntentos() {
		return 0;
	}

	@Override
	public void iniciaIntentos() {
	}

	@Override
	public void aumentaIntentos() {
	}

	@Override
	public boolean muestraPesoPantalla() {
		return false;
	}

}
