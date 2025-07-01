
package com.comerzzia.pos.devices.drivers.escpos.escpos;

import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import com.comerzzia.pos.devices.drivers.escpos.EscPosDevicePrinter;
import com.comerzzia.pos.devices.drivers.escpos.EscPosTicketPrinterException;


public class EscPosDevicePrinterPlain implements EscPosDevicePrinter  {
    
    private static final byte[] NEW_LINE = {0x0D, 0x0A}; // Print and carriage return
      
    private EscPosPrinterWritter out;
    private EscPosUnicodeTranslator trans;
    
    // Creates new TicketPrinter
    public EscPosDevicePrinterPlain(EscPosPrinterWritter CommOutputPrinter) throws EscPosTicketPrinterException {

        out = CommOutputPrinter;
        trans = new EscPosUnicodeTranslatorStar(); // The star translator stands for the 437 int char page
    }
   
    public String getPrinterName() {
        return "Plain";
    }
    public String getPrinterDescription() {
        return null;
    }   
    public JComponent getPrinterComponent() {
        return null;
    }
    public void reset() {
    }
    
    public void beginReceipt() {
    }
    
    public void printImage(BufferedImage image) {
    }
    
    public void printBarCode(String type, String position, String code) {        
        if (! EscPosDevicePrinter.POSITION_NONE.equals(position)) {                
            out.write(code);
            out.write(NEW_LINE);
        }
    }
    
    public void beginLine(int iTextSize) {
    }
    
    public void printText(int iStyle, String sText) {
        out.write(trans.transString(sText));
    }
    
    public void endLine() {
        out.write(NEW_LINE);
    }
    
    public void endReceipt() {       
        out.write(NEW_LINE);
        out.write(NEW_LINE);
        out.write(NEW_LINE);
        out.write(NEW_LINE);
        out.write(NEW_LINE);
        out.flush();
    }
    
    public void openDrawer() {
    }
}

