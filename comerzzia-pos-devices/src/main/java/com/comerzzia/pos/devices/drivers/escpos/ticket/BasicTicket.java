
package com.comerzzia.pos.devices.drivers.escpos.ticket;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class BasicTicket implements PrintItem {

    protected static Font BASEFONT;
    protected static int FONTHEIGHT;
    protected static double IMAGE_SCALE;

    protected java.util.List<PrintItem> m_aCommands;
    protected PrintItemLine pil;
    protected int m_iBodyHeight;

    static {
        BASEFONT = new Font("Monospaced", Font.PLAIN, 12).deriveFont(AffineTransform.getScaleInstance(1.0, 1.40));
        FONTHEIGHT = 20;
        IMAGE_SCALE = 1.0;
    }

    /** Creates a new instance of AbstractTicket */
    public BasicTicket() {
        m_aCommands = new ArrayList<PrintItem>();
        pil = null;
        m_iBodyHeight = 0;
    }

    public int getHeight() {
        return m_iBodyHeight;
    }

    public void draw(Graphics2D g2d, int x, int y, int width) {

        int currenty = y;
        for (PrintItem pi : m_aCommands) {
            pi.draw(g2d, x, currenty, width);
            currenty += pi.getHeight();
        }
    }

    public java.util.List<PrintItem> getCommands() {
        return m_aCommands;
    }

    // INTERFAZ PRINTER 2
    public void printImage(BufferedImage image) {

        PrintItem pi = new PrintItemImage(image, IMAGE_SCALE);
        m_aCommands.add(pi);
        m_iBodyHeight += pi.getHeight();
    }

    public void printBarCode(String type, String position, String code) {

        PrintItem pi = new PrintItemBarcode(type, position, code, IMAGE_SCALE);
        m_aCommands.add(pi);
        m_iBodyHeight += pi.getHeight();
    }

    public void beginLine(int iTextSize) {
        pil = new PrintItemLine(iTextSize, BASEFONT, FONTHEIGHT);
    }

    public void printText(int iStyle, String sText) {
        if (pil != null) {
            pil.addText(iStyle, sText);
        }
    }

    public void endLine() {
        if (pil != null) {
            m_aCommands.add(pil);
            m_iBodyHeight += pil.getHeight();
            pil = null;
        }
    }
}
