package com.comerzzia.pos.devices.printer;

import jpos.JposException;

public class DevicePrinterUPOSNCR extends DevicePrinterUPOS {
    @Override
    public void commandBeginBarcode(String comandoEntradaCodBar) {
    	if (comandoEntradaCodBar != null) {    		
    		Integer parteComandoInt = new Integer(comandoEntradaCodBar);
	    	try {
	    		final int NCRDIO_PTR_SET_BARCODE_WIDTH = 102;
				impresora.getDevice().directIO(NCRDIO_PTR_SET_BARCODE_WIDTH, new int[]{parteComandoInt.intValue()}, null);
			} catch (JposException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}        
    }
}
