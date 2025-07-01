
package com.comerzzia.pos.devices.drivers.escpos.escpos;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;

import com.comerzzia.pos.devices.drivers.escpos.EscPosTicketPrinterException;

import ch.ntb.usb.Device;
import ch.ntb.usb.USB;
import ch.ntb.usb.USBException;

public class EscPosPrinterWritterUsb extends EscPosPrinterWritter {

	private static Logger log = Logger.getLogger(EscPosPrinterWritterUsb.class);
	
    private String m_sPortPrinter;

    private short vendorId;
    private short productId;
    private String bus;
    private String file;
    private int writingZone;
    Integer numero;

    Device dev = null;

    public EscPosPrinterWritterUsb(String sPortPrinter)
            throws EscPosTicketPrinterException {
        String[] cadena = sPortPrinter.split(",");
        m_sPortPrinter = cadena[0];
        String[] portData = m_sPortPrinter.split("-");
        vendorId = Short.parseShort(portData[0], 16);
        productId = Short.parseShort(portData[1], 16);
        bus = portData[2];
        file = portData[3];
        if (portData[2].equals("null")) {
            bus = null;
        }
        if (portData[3].equals("null")) {
            file = null;
        }
        writingZone = Integer.parseInt(portData[4], 16);
        numero = Integer.parseInt(cadena[1]);
        log.debug("Inciando dispositivo usb: vendorId[" + vendorId + "] productId[" + productId + "] bus[" + bus + "] archivo[" + file + "] filename[" + file + "] writingZone[" + writingZone + "] con numero[" + numero + "]");
        dev = null;
    }

    protected void internalWrite(byte[] data) {
        try {
            if (dev == null) {
                dev = USB.getDevice(vendorId, productId, bus, file);
                dev.open(1, 0, -1);
            }
        }
        catch (USBException e) {
            log.error("Error abriendo dispositivo usb: bus[" + bus + "] archivo[" + file + "] filename[" + file + "] writingZone[" + writingZone + "] con numero[" + numero + "]");
        }
        try {
            if (data.length != 0) {
                dev.writeBulk(writingZone, data, data.length, 2000, false);
            }
        }
        catch (USBException e) {
            try {
				log.error("Error escribiendo \"" + new String(data, "UTF-8") + "\" (" + data.length+" bytes) en el dispositivo usb: bus[" + bus + "] archivo[" + file + "] filename[" + file + "] writingZone[" + writingZone + "] con numero[" + numero + "]", e);
			} catch (UnsupportedEncodingException e1) {
				log.error("internalWrite() - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
			}
        }
    }

    protected void internalFlush() {
        // Nothing TO DO explicit ESC/POS Flushing is required.
        return;
    }

    protected void internalClose() {
        try {
            if (dev != null) {
                dev.close();
                dev = null;
            }
        }
        catch (IOException e) {
            log.error("internalClose() - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
        }
    }
}
