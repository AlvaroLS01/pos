
package com.comerzzia.pos.devices.drivers.escpos;

public class EscPosNullAnimator implements EscPosDisplayAnimator {
    
    protected String currentLine1;
    protected String currentLine2; 
    
    public EscPosNullAnimator(String line1, String line2) {
        currentLine1 = EscPosDeviceTicket.alignLeft(line1, 20);
        currentLine2 = EscPosDeviceTicket.alignLeft(line2, 20);
    }

    public void setTiming(int i) {
    }

    public String getLine1() {
        return currentLine1;
    }

    public String getLine2() {
        return currentLine2;
    }
}
