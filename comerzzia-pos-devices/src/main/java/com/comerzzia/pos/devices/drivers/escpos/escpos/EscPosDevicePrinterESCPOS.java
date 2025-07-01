
package com.comerzzia.pos.devices.drivers.escpos.escpos;

import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import com.comerzzia.pos.devices.drivers.escpos.EscPosDevicePrinter;
import com.comerzzia.pos.devices.drivers.escpos.EscPosTicketPrinterException;

public class EscPosDevicePrinterESCPOS implements EscPosDevicePrinter {

    private EscPosPrinterWritter m_CommOutputPrinter;
    private EscPosCodes m_codes;
    private EscPosUnicodeTranslator m_trans;

//    private boolean m_bInline;
    private String m_sName;

    // Creates new TicketPrinter
    public EscPosDevicePrinterESCPOS(EscPosPrinterWritter CommOutputPrinter, EscPosCodes codes, EscPosUnicodeTranslator trans) throws EscPosTicketPrinterException {

        m_sName = "PrinterSerial";
        m_CommOutputPrinter = CommOutputPrinter;
        m_codes = codes;
        m_trans = trans;

        // Inicializamos la impresora
        m_CommOutputPrinter.init(ESCPOS.INIT);

        m_CommOutputPrinter.write(ESCPOS.SELECT_PRINTER); // A la impresora
        m_CommOutputPrinter.init(m_codes.getInitSequence());
        m_CommOutputPrinter.write(m_trans.getCodeTable());

        m_CommOutputPrinter.flush();
    }

    public String getPrinterName() {
        return m_sName;
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

        m_CommOutputPrinter.write(ESCPOS.SELECT_PRINTER);
        m_CommOutputPrinter.write(m_codes.transImage(image));
    }

    public void printBarCode(String type, String position, String code) {

        m_CommOutputPrinter.write(ESCPOS.SELECT_PRINTER);
        m_codes.printBarcode(m_CommOutputPrinter, type, position, code);
    }

    public void internalClose() {
        m_CommOutputPrinter.internalClose();
    }

    public void beginLine(int iTextSize) {

        m_CommOutputPrinter.write(ESCPOS.SELECT_PRINTER);

        if (iTextSize == EscPosDevicePrinter.SIZE_0) {
            m_CommOutputPrinter.write(m_codes.getSize0());
        }
        else if (iTextSize == EscPosDevicePrinter.SIZE_1) {
            m_CommOutputPrinter.write(m_codes.getSize1());
        }
        else if (iTextSize == EscPosDevicePrinter.SIZE_2) {
            m_CommOutputPrinter.write(m_codes.getSize2());
        }
        else if (iTextSize == EscPosDevicePrinter.SIZE_3) {
            m_CommOutputPrinter.write(m_codes.getSize3());
        }
        else {
            m_CommOutputPrinter.write(m_codes.getSize0());
        }
    }

    public void printText(int iStyle, String sText) {

        m_CommOutputPrinter.write(ESCPOS.SELECT_PRINTER);

        if ((iStyle & EscPosDevicePrinter.STYLE_BOLD) != 0) {
            m_CommOutputPrinter.write(m_codes.getBoldSet());
        }
        if ((iStyle & EscPosDevicePrinter.STYLE_UNDERLINE) != 0) {
            m_CommOutputPrinter.write(m_codes.getUnderlineSet());
        }
        m_CommOutputPrinter.write(m_trans.transString(sText));
        if ((iStyle & EscPosDevicePrinter.STYLE_UNDERLINE) != 0) {
            m_CommOutputPrinter.write(m_codes.getUnderlineReset());
        }
        if ((iStyle & EscPosDevicePrinter.STYLE_BOLD) != 0) {
            m_CommOutputPrinter.write(m_codes.getBoldReset());
        }
    }

    public void endLine() {
        m_CommOutputPrinter.write(ESCPOS.SELECT_PRINTER);
        m_CommOutputPrinter.write(m_codes.getNewLine());
    }

    public void endReceipt() {
        m_CommOutputPrinter.write(ESCPOS.SELECT_PRINTER);

        m_CommOutputPrinter.write(m_codes.getNewLine());
        m_CommOutputPrinter.write(m_codes.getNewLine());
        m_CommOutputPrinter.write(m_codes.getNewLine());
        m_CommOutputPrinter.write(m_codes.getNewLine());
        m_CommOutputPrinter.write(m_codes.getNewLine());

        m_CommOutputPrinter.write(m_codes.getCutReceipt());
        m_CommOutputPrinter.flush();
    }
    
    public void cutPaper(){
        m_CommOutputPrinter.write(m_codes.getCutReceipt());
    }

    public void openDrawer() {

        m_CommOutputPrinter.write(ESCPOS.SELECT_PRINTER);
        m_CommOutputPrinter.write(m_codes.getOpenDrawer());
        m_CommOutputPrinter.flush();
    }
}
