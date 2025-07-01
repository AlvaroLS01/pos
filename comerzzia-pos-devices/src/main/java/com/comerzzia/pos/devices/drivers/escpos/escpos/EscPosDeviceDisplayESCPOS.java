
package com.comerzzia.pos.devices.drivers.escpos.escpos;

import com.comerzzia.pos.devices.drivers.escpos.EscPosDeviceTicket;

public class EscPosDeviceDisplayESCPOS extends EscPosDeviceDisplaySerial {
       
    private EscPosUnicodeTranslator trans;

    /** Creates a new instance of DeviceDisplayESCPOS */
    public EscPosDeviceDisplayESCPOS(EscPosPrinterWritter display, EscPosUnicodeTranslator trans) {
        this.trans = trans;
        init(display);       
    }

    @Override
    public void initVisor() {
        display.init(ESCPOS.INIT);
        display.write(ESCPOS.SELECT_DISPLAY); // Al visor
        display.write(trans.getCodeTable());
        display.write(ESCPOS.VISOR_HIDE_CURSOR);         
        display.write(ESCPOS.VISOR_CLEAR);
        display.write(ESCPOS.VISOR_HOME);
        display.flush();
    }

    public void repaintLines() {
        display.write(ESCPOS.SELECT_DISPLAY);
        display.write(ESCPOS.VISOR_CLEAR);
        display.write(ESCPOS.VISOR_HOME);
        display.write(trans.transString(EscPosDeviceTicket.alignLeft(m_displaylines.getLine1(), 20)));
        display.write(trans.transString(EscPosDeviceTicket.alignLeft(m_displaylines.getLine2(), 20)));        
        display.flush();
    }
    
    public void repaintLine() {
      display.write(m_displaylines.getLine1());   
      display.flush();
  }
}
