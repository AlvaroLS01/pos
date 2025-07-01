
package com.comerzzia.pos.devices.drivers.escpos.ticket;

import java.awt.Graphics2D;

public interface PrintItem {
    
    public int getHeight();
    public void draw(Graphics2D g, int x, int y, int width);
}
