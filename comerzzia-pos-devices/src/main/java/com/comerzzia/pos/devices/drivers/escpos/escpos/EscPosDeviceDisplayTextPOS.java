package com.comerzzia.pos.devices.drivers.escpos.escpos;

import org.apache.commons.lang.StringUtils;

public class EscPosDeviceDisplayTextPOS extends EscPosDeviceDisplayESCPOS {

	public EscPosDeviceDisplayTextPOS(EscPosPrinterWritter display, EscPosUnicodeTranslator trans) {
		super(display, trans);
	}
	
	@Override
	public void initVisor() {
		String cadenaLimpiar = "\r" + StringUtils.rightPad(" ", 20);

		display.write(cadenaLimpiar + "\n\r");
		display.write(cadenaLimpiar + "\n\r");
    	display.flush();
	}

}
