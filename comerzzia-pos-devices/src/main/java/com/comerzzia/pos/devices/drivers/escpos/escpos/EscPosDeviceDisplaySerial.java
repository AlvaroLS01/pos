
package com.comerzzia.pos.devices.drivers.escpos.escpos;

import com.comerzzia.pos.devices.drivers.escpos.EscPosDeviceDisplay;
import com.comerzzia.pos.devices.drivers.escpos.EscPosDeviceDisplayBase;
import com.comerzzia.pos.devices.drivers.escpos.EscPosDeviceDisplayImpl;

public abstract class EscPosDeviceDisplaySerial implements EscPosDeviceDisplay, EscPosDeviceDisplayImpl {
    
    private String m_sName;    
    protected EscPosPrinterWritter display;  
    
    protected EscPosDeviceDisplayBase m_displaylines;
    
    public EscPosDeviceDisplaySerial() {
        m_displaylines = new EscPosDeviceDisplayBase(this);
    }
    
    protected void init(EscPosPrinterWritter display) {                
        m_sName = "VisorSerial";
        this.display = display;      
        initVisor();        
    }
   
    public String getDisplayName() {
        return m_sName;
    }    
    public String getDisplayDescription() {
        return null;
    }        
    public javax.swing.JComponent getDisplayComponent() {
        return null;
    }
    
    public void writeVisor(int animation, String sLine1, String sLine2) {
        m_displaylines.writeVisor(animation, sLine1, sLine2);
    }    
    
    public void writeVisor(String sLine1, String sLine2) {        
        m_displaylines.writeVisor(sLine1, sLine2);
    }
    
    public void writeVisor(String sLine) {        
        m_displaylines.writeVisor(sLine);
    }
     
    public void clearVisor() {
        m_displaylines.clearVisor();
    }
    
    public abstract void initVisor();
}
