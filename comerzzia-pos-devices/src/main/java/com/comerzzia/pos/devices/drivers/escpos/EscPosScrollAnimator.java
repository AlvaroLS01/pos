
package com.comerzzia.pos.devices.drivers.escpos;

public class EscPosScrollAnimator extends EscPosBaseAnimator {

    private int msglength;

    public EscPosScrollAnimator(String line1, String line2) {
        msglength = Math.max(line1.length(), line2.length());
        baseLine1 = EscPosDeviceTicket.alignLeft(line1, msglength);
        baseLine2 = EscPosDeviceTicket.alignLeft(line2, msglength);
    }

    public void setTiming(int i) {
        int j = (i / 2) % (msglength + 20);
        if (j < 20) {
            currentLine1 = EscPosDeviceTicket.alignLeft(EscPosDeviceTicket.getWhiteString(20 - j) + baseLine1, 20);
            currentLine2 = EscPosDeviceTicket.alignLeft(EscPosDeviceTicket.getWhiteString(20 - j) + baseLine2, 20);
        } else {
            currentLine1 = EscPosDeviceTicket.alignLeft(baseLine1.substring(j - 20), 20);
            currentLine2 = EscPosDeviceTicket.alignLeft(baseLine2.substring(j - 20), 20);
        }
    }
}
