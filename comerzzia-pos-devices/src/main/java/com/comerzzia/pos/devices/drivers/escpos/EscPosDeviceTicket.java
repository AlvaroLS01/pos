
package com.comerzzia.pos.devices.drivers.escpos;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.comerzzia.pos.devices.drivers.escpos.escpos.EscPosCodesEpson;
import com.comerzzia.pos.devices.drivers.escpos.escpos.EscPosCodesIthaca;
import com.comerzzia.pos.devices.drivers.escpos.escpos.EscPosCodesStar;
import com.comerzzia.pos.devices.drivers.escpos.escpos.EscPosCodesSurePOS;
import com.comerzzia.pos.devices.drivers.escpos.escpos.EscPosCodesTMU220;
import com.comerzzia.pos.devices.drivers.escpos.escpos.EscPosDeviceDisplayESCPOS;
import com.comerzzia.pos.devices.drivers.escpos.escpos.EscPosDeviceDisplaySurePOS;
import com.comerzzia.pos.devices.drivers.escpos.escpos.EscPosDevicePrinterESCPOS;
import com.comerzzia.pos.devices.drivers.escpos.escpos.EscPosDevicePrinterPlain;
import com.comerzzia.pos.devices.drivers.escpos.escpos.EscPosPrinterWritter;
import com.comerzzia.pos.devices.drivers.escpos.escpos.EscPosPrinterWritterFile;
import com.comerzzia.pos.devices.drivers.escpos.escpos.EscPosUnicodeTranslatorEur;
import com.comerzzia.pos.devices.drivers.escpos.escpos.EscPosUnicodeTranslatorInt;
import com.comerzzia.pos.devices.drivers.escpos.escpos.EscPosUnicodeTranslatorStar;
import com.comerzzia.pos.devices.drivers.escpos.escpos.EscPosUnicodeTranslatorSurePOS;
import com.comerzzia.pos.devices.drivers.escpos.printer.EscPosDevicePrinterPrinter;
import com.comerzzia.pos.devices.drivers.escpos.util.StringParser;

public class EscPosDeviceTicket {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(EscPosDeviceTicket.class.getName());

    private EscPosDeviceFiscalPrinter m_deviceFiscal;
    private EscPosDeviceDisplay m_devicedisplay;
    private EscPosDevicePrinter m_nullprinter;
    private Map<String, EscPosDevicePrinter> m_deviceprinters;
    private List<EscPosDevicePrinter> m_deviceprinterslist;

    /** Creates a new instance of DeviceTicket */
    public EscPosDeviceTicket() {
        // Una impresora solo de pantalla.

        m_deviceFiscal = new EscPosDeviceFiscalPrinterNull();

        m_devicedisplay = new EscPosDeviceDisplayNull();

        m_nullprinter = new EscPosDevicePrinterNull();
        m_deviceprinters = new HashMap<String, EscPosDevicePrinter>();
        m_deviceprinterslist = new ArrayList<EscPosDevicePrinter>();
    }

    public EscPosDeviceTicket(Component parent) {

        PrinterWritterPool pws = new PrinterWritterPool();

        // La impresora fiscal
//        StringParser sf = new StringParser("Fiscal Printer");
//        String sFiscalType = sf.nextToken(':');
//        String sFiscalParam1 = sf.nextToken(',');
        m_deviceFiscal = new EscPosDeviceFiscalPrinterNull();

        // El visor
        StringParser sd = new StringParser("screen");
        String sDisplayType = sd.nextToken(':');
        String sDisplayParam1 = sd.nextToken(',');
        String sDisplayParam2 = sd.nextToken(',');

        // compatibilidad hacia atras.
        if ("serial".equals(sDisplayType) || "rxtx".equals(sDisplayType) || "file".equals(sDisplayType)) {
            sDisplayParam2 = sDisplayParam1;
            sDisplayParam1 = sDisplayType;
            sDisplayType = "epson";
        }

        try {
            if ("epson".equals(sDisplayType)) {
                m_devicedisplay = new EscPosDeviceDisplayESCPOS(pws.getPrinterWritter(sDisplayParam1, sDisplayParam2), new EscPosUnicodeTranslatorInt());
            } else if ("surepos".equals(sDisplayType)) {
                m_devicedisplay = new EscPosDeviceDisplaySurePOS(pws.getPrinterWritter(sDisplayParam1, sDisplayParam2));
            } else if ("ld200".equals(sDisplayType)) {
                m_devicedisplay = new EscPosDeviceDisplayESCPOS(pws.getPrinterWritter(sDisplayParam1, sDisplayParam2), new EscPosUnicodeTranslatorEur());
            } else {
                m_devicedisplay = new EscPosDeviceDisplayNull();
            }
        } catch (EscPosTicketPrinterException e) {
            log.warn(e.getMessage(), e);
            m_devicedisplay = new EscPosDeviceDisplayNull(e.getMessage());
        }

        m_nullprinter = new EscPosDevicePrinterNull();
        m_deviceprinters = new HashMap<String, EscPosDevicePrinter>();
        m_deviceprinterslist = new ArrayList<EscPosDevicePrinter>();

        // Empezamos a iterar por las impresoras...
        int iPrinterIndex = 1;
        String sPrinterIndex = Integer.toString(iPrinterIndex);
        String sprinter = "window";

        while (sprinter != null && !"".equals(sprinter)) {

            StringParser sp = new StringParser(sprinter);
            String sPrinterType = sp.nextToken(':');
            String sPrinterParam1 = sp.nextToken(',');
            String sPrinterParam2 = sp.nextToken(',');

            // compatibilidad hacia atras.
            if ("serial".equals(sPrinterType) || "rxtx".equals(sPrinterType) || "file".equals(sPrinterType)) {
                sPrinterParam2 = sPrinterParam1;
                sPrinterParam1 = sPrinterType;
                sPrinterType = "epson";
            }

            try {
                if ("printer".equals(sPrinterType)) {

                    // backward compatibility
                    if (sPrinterParam2 == null || sPrinterParam2.equals("") || sPrinterParam2.equals("true")) {
                        sPrinterParam2 = "receipt";
                    } else if (sPrinterParam2.equals("false")) {
                        sPrinterParam2 = "standard";
                    }

                    addPrinter(sPrinterIndex, new EscPosDevicePrinterPrinter(
                            sPrinterParam1, 10, 287, 15, 297, "A4"));
                } else if ("epson".equals(sPrinterType)) {
                    addPrinter(sPrinterIndex, new EscPosDevicePrinterESCPOS(pws.getPrinterWritter(sPrinterParam1, sPrinterParam2), new EscPosCodesEpson(), new EscPosUnicodeTranslatorInt()));
                } else if ("tmu220".equals(sPrinterType)) {
                    addPrinter(sPrinterIndex, new EscPosDevicePrinterESCPOS(pws.getPrinterWritter(sPrinterParam1, sPrinterParam2), new EscPosCodesTMU220(), new EscPosUnicodeTranslatorInt()));
                } else if ("star".equals(sPrinterType)) {
                    addPrinter(sPrinterIndex, new EscPosDevicePrinterESCPOS(pws.getPrinterWritter(sPrinterParam1, sPrinterParam2), new EscPosCodesStar(), new EscPosUnicodeTranslatorStar()));
                } else if ("ithaca".equals(sPrinterType)) {
                    addPrinter(sPrinterIndex, new EscPosDevicePrinterESCPOS(pws.getPrinterWritter(sPrinterParam1, sPrinterParam2), new EscPosCodesIthaca(), new EscPosUnicodeTranslatorInt()));
                } else if ("surepos".equals(sPrinterType)) {
                    addPrinter(sPrinterIndex, new EscPosDevicePrinterESCPOS(pws.getPrinterWritter(sPrinterParam1, sPrinterParam2), new EscPosCodesSurePOS(), new EscPosUnicodeTranslatorSurePOS()));
                } else if ("plain".equals(sPrinterType)) {
                    addPrinter(sPrinterIndex, new EscPosDevicePrinterPlain(pws.getPrinterWritter(sPrinterParam1, sPrinterParam2)));
                }
            } catch (EscPosTicketPrinterException e) {
                log.warn(e.getMessage(), e);
            }

            // siguiente impresora...
            iPrinterIndex++;
            sPrinterIndex = Integer.toString(iPrinterIndex);
            sprinter = "";
        }
    }

    private void addPrinter(String sPrinterIndex, EscPosDevicePrinter p) {
        m_deviceprinters.put(sPrinterIndex, p);
        m_deviceprinterslist.add(p);
    }

    private static class PrinterWritterPool {

        private Map<String, EscPosPrinterWritter> m_apool = new HashMap<String, EscPosPrinterWritter>();

        public EscPosPrinterWritter getPrinterWritter(String con, String port) throws EscPosTicketPrinterException {

            String skey = con + "-->" + port;
            EscPosPrinterWritter pw = (EscPosPrinterWritter) m_apool.get(skey);
            if (pw == null) {
                if ("serial".equals(con) || "rxtx".equals(con)) {
                	//TODO
//                    pw = new PrinterWritterRXTX(port);
                    m_apool.put(skey, pw);
                } else if ("file".equals(con)) {
                    pw = new EscPosPrinterWritterFile(port);
                    m_apool.put(skey, pw);
                } else {
                    log.error("getPrinterWritter() - Error obteniendo gestor de impresión");
                    throw new EscPosTicketPrinterException();
                }
            }
            return pw;
        }
    }
    // Impresora fiscal
    public EscPosDeviceFiscalPrinter getFiscalPrinter() {
        return m_deviceFiscal;
    }
    // Display
    public EscPosDeviceDisplay getDeviceDisplay() {
        return m_devicedisplay;
    }
    // Receipt printers
    public EscPosDevicePrinter getDevicePrinter(String key) {
        EscPosDevicePrinter printer = m_deviceprinters.get(key);
        return printer == null ? m_nullprinter : printer;
    }

    public List<EscPosDevicePrinter> getDevicePrinterAll() {
        return m_deviceprinterslist;
    }
    // Utilidades
    public static String getWhiteString(int iSize, char cWhiteChar) {

        char[] cFill = new char[iSize];
        for (int i = 0; i < iSize; i++) {
            cFill[i] = cWhiteChar;
        }
        return new String(cFill);
    }

    public static String getWhiteString(int iSize) {

        return getWhiteString(iSize, ' ');
    }

    public static String alignBarCode(String sLine, int iSize) {

        if (sLine.length() > iSize) {
            return sLine.substring(sLine.length() - iSize);
        } else {
            return getWhiteString(iSize - sLine.length(), '0') + sLine;
        }
    }

    public static String alignLeft(String sLine, int iSize) {

        if (sLine.length() > iSize) {
            return sLine.substring(0, iSize);
        } else {
            return sLine + getWhiteString(iSize - sLine.length());
        }
    }

    public static String alignRight(String sLine, int iSize) {

        if (sLine.length() > iSize) {
            return sLine.substring(sLine.length() - iSize);
        } else {
            return getWhiteString(iSize - sLine.length()) + sLine;
        }
    }

    public static String alignCenter(String sLine, int iSize) {

        if (sLine.length() > iSize) {
            return alignRight(sLine.substring(0, (sLine.length() + iSize) / 2), iSize);
        } else {
            return alignRight(sLine + getWhiteString((iSize - sLine.length()) / 2), iSize);
        }
    }

    public static String alignCenter(String sLine) {
        return alignCenter(sLine, 42);
    }

    public static final byte[] transNumber(String sCad) {

        if (sCad == null) {
            return null;
        } else {
            byte bAux[] = new byte[sCad.length()];
            for( int i = 0; i < sCad.length(); i++) {
                bAux[i] = transNumberChar(sCad.charAt(i));
            }
            return bAux;
        }
    }

    public static byte transNumberChar(char sChar) {
        switch (sChar) {
        case '0' : return 0x30;
        case '1' : return 0x31;
        case '2' : return 0x32;
        case '3' : return 0x33;
        case '4' : return 0x34;
        case '5' : return 0x35;
        case '6' : return 0x36;
        case '7' : return 0x37;
        case '8' : return 0x38;
        case '9' : return 0x39;
        default: return 0x30;
        }
    }
}
