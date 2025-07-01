
package com.comerzzia.pos.devices.drivers.escpos;

public class EscPosCurtainAnimator extends EscPosBaseAnimator {
    
    public EscPosCurtainAnimator(String line1, String line2) {
        baseLine1 = EscPosDeviceTicket.alignLeft(line1, 20);
        baseLine2 = EscPosDeviceTicket.alignLeft(line2, 20);
    }
    
    public void setTiming(int i) {
        
        int j = i / 2;

        if (j < 10) {
            currentLine1 = EscPosDeviceTicket.alignCenter(baseLine1.substring(10 - j, 10 + j), 20);
            currentLine2 = EscPosDeviceTicket.alignCenter(baseLine2.substring(10 - j, 10 + j), 20);
        } else {
            currentLine1 = baseLine1;
            currentLine2 = baseLine2;
        }
    }
}