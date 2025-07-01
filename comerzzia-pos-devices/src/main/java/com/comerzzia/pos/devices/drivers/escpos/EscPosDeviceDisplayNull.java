
package com.comerzzia.pos.devices.drivers.escpos;

public class EscPosDeviceDisplayNull implements EscPosDeviceDisplay {
    
    private String m_sName;
    private String m_sDescription;
    
    /** Creates a new instance of DeviceDisplayNull */
    public EscPosDeviceDisplayNull() {
        this(null);
    }
    
    /** Creates a new instance of DeviceDisplayNull */
    public EscPosDeviceDisplayNull(String desc) {
        m_sName = "Display null";
        m_sDescription = desc;
    }

    public String getDisplayName() {
        return m_sName;
    }    
    public String getDisplayDescription() {
        return m_sDescription;
    }        
    public javax.swing.JComponent getDisplayComponent() {
        return null;
    }
    
    public void clearVisor() {
    }      
    public void writeVisor(String sLine1, String sLine2) {
    } 
    public void writeVisor(int animation, String sLine1, String sLine2) {
    } 
}
