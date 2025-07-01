
package com.comerzzia.pos.devices.drivers.escpos;

public class EscPosFlyerAnimator extends EscPosBaseAnimator {
    
    public EscPosFlyerAnimator(String line1, String line2) {
        baseLine1 = EscPosDeviceTicket.alignLeft(line1, 20);
        baseLine2 = EscPosDeviceTicket.alignLeft(line2, 20);
    }
    
    public void setTiming(int i) {

        if (i < 20) {
            currentLine1 = EscPosDeviceTicket.alignRight(baseLine1.substring(0, i), 20);
            currentLine2 = EscPosDeviceTicket.alignRight(baseLine2.substring(0, i), 20);
        } else {
            currentLine1 = baseLine1;
            currentLine2 = baseLine2;
        }
    }
}
