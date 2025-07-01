package com.comerzzia.pos.devices.drivers.escpos.escpos;

import com.comerzzia.pos.devices.drivers.escpos.EscPosDeviceTicket;

public class EscPosDeviceDisplayNcr extends EscPosDeviceDisplayESCPOS {

	private EscPosUnicodeTranslator trans;

	public EscPosDeviceDisplayNcr(EscPosPrinterWritter display, EscPosUnicodeTranslator trans) {
		super(display, trans);
		this.trans = trans;
		initVisorNcr();
	}

	public void initVisorNcr() {
		display.init(EscPosConstantsNcr.INIT_NCR);
		display.write(EscPosConstantsNcr.SELECT_DISPLAY_NCR); // Al visor
		display.write(trans.getCodeTable());
		display.write(EscPosConstantsNcr.VISOR_CLEAR_NCR);
		display.write(EscPosConstantsNcr.VISOR_HOME_NCR);
		display.flush();
	}

	@Override
	public void repaintLines() {
		display.write(EscPosConstantsNcr.SELECT_DISPLAY_NCR);
        display.write(EscPosConstantsNcr.VISOR_CLEAR_NCR);
        display.write(EscPosConstantsNcr.VISOR_HOME_NCR);
        display.write(trans.transString(EscPosDeviceTicket.alignLeft(m_displaylines.getLine1(), 20)));
        display.write(trans.transString(EscPosDeviceTicket.alignLeft(m_displaylines.getLine2(), 20)));
        display.flush();
	}

}
