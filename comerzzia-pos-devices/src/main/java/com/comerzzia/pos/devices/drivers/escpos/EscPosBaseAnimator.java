
package com.comerzzia.pos.devices.drivers.escpos;

public abstract class EscPosBaseAnimator implements EscPosDisplayAnimator {
    
    protected String baseLine1;
    protected String baseLine2;
    protected String currentLine1;
    protected String currentLine2; 
    
    public EscPosBaseAnimator() {
        baseLine1 = null;
        baseLine2 = null;
    }

    public EscPosBaseAnimator(String line1, String line2) {
        baseLine1 = line1;
        baseLine2 = line2;
    }

    public String getLine1() {
        return currentLine1;
    }

    public String getLine2() {
        return currentLine2;
    }
}
