
package com.comerzzia.pos.devices.drivers.escpos;

public class EscPosBlinkAnimator extends EscPosBaseAnimator {
    
    public EscPosBlinkAnimator(String line1, String line2) {
        baseLine1 = EscPosDeviceTicket.alignLeft(line1, 20);
        baseLine2 = EscPosDeviceTicket.alignLeft(line2, 20);
    }
    
    public void setTiming(int i) {
        
        if ((i % 10) < 5) {
            currentLine1 = "";
            currentLine2 = "";
        } else {
            currentLine1 = baseLine1;
            currentLine2 = baseLine2;
        }
    }
}
