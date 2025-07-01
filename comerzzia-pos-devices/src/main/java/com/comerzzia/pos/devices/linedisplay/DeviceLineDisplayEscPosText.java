package com.comerzzia.pos.devices.linedisplay;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.comerzzia.pos.core.devices.configuration.DeviceConfiguration;
import com.comerzzia.pos.devices.drivers.escpos.escpos.EscPosDeviceDisplayESCPOS;
import com.comerzzia.pos.devices.drivers.escpos.escpos.EscPosDeviceDisplayTextPOS;
import com.comerzzia.pos.devices.drivers.escpos.escpos.EscPosPrinterWritter;
import com.comerzzia.pos.devices.drivers.escpos.escpos.EscPosUnicodeTranslator;
import com.comerzzia.pos.util.xml.XMLDocumentNode;
import com.comerzzia.pos.util.xml.XMLDocumentNodeNotFoundException;

public class DeviceLineDisplayEscPosText extends DeviceLineDisplayEscPos {
	
	public static final String TAG_LINE_MAX_CHARS = "lineMaxChars";

	private static final Logger log = Logger.getLogger(DeviceLineDisplayEscPosText.class.getName());

	private int lineMaxChars;
	
	@Override
	public EscPosDeviceDisplayESCPOS crearManejadorVisor(EscPosPrinterWritter printerWritter, EscPosUnicodeTranslator translator) {
		return new EscPosDeviceDisplayTextPOS(printerWritter, translator);
	}

	@Override
	protected void loadConfiguration(DeviceConfiguration config) {
		super.loadConfiguration(config);

		XMLDocumentNode configConexion = config.getModelConfiguration().getConnectionConfig();

		try {
			lineMaxChars = configConexion.getNode(TAG_LINE_MAX_CHARS).getValueAsInteger();
		}
		catch (XMLDocumentNodeNotFoundException e) {
			log.error("cargaConfiguracion() - No se han encontrado los parámetros de configuración. Se pondrán valores por defecto: " + e.getMessage(), e);

			lineMaxChars = 20;
		}
	}

	@Override
	public void write(String cadena1, String cadena2) {
		limpiarVisor();
		visor.writeVisor("\r" + StringUtils.rightPad(cadena1.trim(), lineMaxChars));
		writeLineDown(cadena2);
	}

	private void limpiarVisor() {
		log.trace("limpiarVisor() - Limpiando visor...");
		String cadenaLimpiar = "\r" + StringUtils.rightPad(" ", lineMaxChars);

		visor.writeVisor(cadenaLimpiar + "\n\r");
		visor.writeVisor(cadenaLimpiar + "\n\r");

	}
	
	@Override
	public void writeLineUp(String cadena) {
		log.trace("escribirLineaArriba(): " + cadena);
		limpiarVisor();
		visor.writeVisor("\r" + StringUtils.rightPad(cadena.trim(), lineMaxChars));
		writeLineDown("");
	}
	
	@Override
	public void writeLineDown(String cadena) {
		log.trace("escribirLineaAbajo(): " + cadena);
		visor.writeVisor("\r\n" + StringUtils.rightPad(cadena.trim(), lineMaxChars) + "\n\r");
	}
	
	@Override
	public void writeRightUp(String cadena) {
		log.trace("escribirDerechaArriba(): " + cadena);
		limpiarVisor();
		super.writeRightUp("\r" + StringUtils.leftPad(cadena.trim(), lineMaxChars));
	}
	
	
	@Override
	public void writeRightDown(String cadena) {
		log.trace("escribirDerechaAbajo(): " + cadena);
		super.writeRightDown("\r\n" + StringUtils.leftPad(cadena.trim(), lineMaxChars) + "\n\r");
	}
}
