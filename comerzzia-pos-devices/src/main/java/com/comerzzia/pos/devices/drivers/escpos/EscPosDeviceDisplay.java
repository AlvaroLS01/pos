
package com.comerzzia.pos.devices.drivers.escpos;

import javax.swing.JComponent;

public interface EscPosDeviceDisplay {

    // INTERFAZ DESCRIPCION
    public String getDisplayName();
    public String getDisplayDescription();
    public JComponent getDisplayComponent();
    
    // INTERFAZ VISOR    
    public void writeVisor(int animation, String sLine1, String sLine2);
    public void writeVisor(String sLine1, String sLine2);
    public void clearVisor();
}
