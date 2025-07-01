package com.comerzzia.pos.devices.linedisplay;

import com.comerzzia.pos.devices.drivers.escpos.escpos.EscPosDeviceDisplayESCPOS;
import com.comerzzia.pos.devices.drivers.escpos.escpos.EscPosDeviceDisplayNcr;
import com.comerzzia.pos.devices.drivers.escpos.escpos.EscPosPrinterWritter;
import com.comerzzia.pos.devices.drivers.escpos.escpos.EscPosUnicodeTranslator;


public class DeviceLineDisplayNCR extends DeviceLineDisplayEscPos {
	
	@Override
	public EscPosDeviceDisplayESCPOS crearManejadorVisor(EscPosPrinterWritter printerWritter, EscPosUnicodeTranslator translator) {
		return new EscPosDeviceDisplayNcr(printerWritter, translator);
	}

}
