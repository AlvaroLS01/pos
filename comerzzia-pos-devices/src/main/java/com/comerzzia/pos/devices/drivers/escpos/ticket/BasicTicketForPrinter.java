
package com.comerzzia.pos.devices.drivers.escpos.ticket;

import java.awt.Font;
import java.awt.geom.AffineTransform;

public class BasicTicketForPrinter extends BasicTicket {

    static {
//        BASEFONT = new Font("Monospaced", Font.PLAIN, 7).deriveFont(AffineTransform.getScaleInstance(1.0, 1.50));
//        FONTHEIGHT = 14;
        BASEFONT = new Font("Monospaced", Font.PLAIN, 7).deriveFont(AffineTransform.getScaleInstance(1.0, 1.40));
        FONTHEIGHT = 12;
        IMAGE_SCALE = 0.65;
    }
}
