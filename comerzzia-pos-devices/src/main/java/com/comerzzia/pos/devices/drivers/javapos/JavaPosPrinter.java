
package com.comerzzia.pos.devices.drivers.javapos;

import java.util.List;

import org.apache.log4j.Logger;

import com.comerzzia.pos.devices.drivers.javapos.facade.exceptions.NoDeviceException;
import com.comerzzia.pos.devices.drivers.javapos.facade.exceptions.NoLogicalNameException;
import com.comerzzia.pos.devices.drivers.javapos.facade.jpos.CommonDeviceCatFacade;
import com.comerzzia.pos.devices.drivers.javapos.facade.jpos.POSPrinterFacade;
import com.comerzzia.pos.devices.drivers.javapos.facade.listener.PosPrinterEventListener;

import jpos.BaseControl;
import jpos.JposException;
import jpos.POSPrinter;

public class JavaPosPrinter extends CommonDeviceCatFacade{

    private static final Logger log = Logger.getLogger(JavaPosPrinter.class.getName());
    
    private static JavaPosPrinter impresora = new JavaPosPrinter();
    
    private POSPrinterFacade posPrinterFacade;
    
    private JavaPosPrinter(){}
    
    public static JavaPosPrinter getImpresora() {
        createImpresora();
        return impresora;
    }
    
    private synchronized static void createImpresora() {
        if (impresora == null){
            impresora = new JavaPosPrinter();
        }
    }

    public void inicializar(String logicalName) throws JposException, NoLogicalNameException, NoDeviceException {
    	log.debug("inicializar()");
        if (posPrinterFacade == null) {
        	log.debug("inicializar() - Inicializando dispositivo");
        	posPrinterFacade = new POSPrinterFacade(logicalName);
        	posPrinterFacade.prepareDevice();
        }
        else {
        	log.debug("inicializar() - No se inicializa el dispositivo de nuevo");
        }
    }
    
    @Override
    public POSPrinter getDevice() {
        return posPrinterFacade.getDevice();
    }

    public PosPrinterEventListener getPosPrinterEventListener() {
        return posPrinterFacade.getPosPrinterEventListener();
    }

    public void setPosPrinterEventListener(PosPrinterEventListener eventListener) {
        posPrinterFacade.setPosPrinterEventListener(eventListener);
    }

    @Override
    public void prepareDevice() throws jpos.JposException, NoLogicalNameException, NoDeviceException {
    	posPrinterFacade.prepareDevice();
    }
    
    public void setRecLineChars(int recLineChars) throws jpos.JposException {
        posPrinterFacade.setRecLineChars(recLineChars);
    }

    public void transactionStart() throws jpos.JposException {
    	transactionStart(true, false);
    }
    
    public void transactionStart(boolean transactionPrint, boolean printInAsyncMode) throws jpos.JposException {
    	log.trace("transactionStart()");
    	posPrinterFacade.transactionStart(transactionPrint, printInAsyncMode);
    }
    
    public void transactionEnd() throws jpos.JposException {
    	log.trace("transactionEnd()");
    	posPrinterFacade.transactionEnd();
    }
    
    public void printNormal(int station, String text) throws jpos.JposException {
        posPrinterFacade.printNormal(station, text);
    }

    public void printInmediate(int station, String text) throws jpos.JposException {
        posPrinterFacade.printInmediate(station, text);
    }

    public void printNormal(String text) throws jpos.JposException {
        posPrinterFacade.printNormal(text);
    }

    public void printInmediate(String text) throws jpos.JposException {
        posPrinterFacade.printInmediate(text);
    }

    public void cutPaper() throws jpos.JposException {
        posPrinterFacade.cutPaper();
    }

    public void printBarCode(int symbology, int height, int width, int alignment, int textPos, String barcode, Integer station) throws jpos.JposException {
        posPrinterFacade.printBarCode(symbology, height, width, alignment, textPos, barcode, station);
    }

    public void printBarCode(String barcode, int symbology) throws jpos.JposException {
        posPrinterFacade.printBarCode(barcode, symbology);
    }

    public void printImage(Integer station, String fileName, int width, int alignment) throws jpos.JposException {
        posPrinterFacade.printImage(station, fileName, width, alignment);
    }

    public void printImage(String fileName) throws jpos.JposException {
        posPrinterFacade.printImage(fileName);
    }

    public void printLineBreak() throws jpos.JposException {
        posPrinterFacade.printLineBreak();
    }

    public void printCentered(String text) throws jpos.JposException {
        posPrinterFacade.printCentered(text);
    }

    public void printRightAlign(String text) throws jpos.JposException {
        posPrinterFacade.printRightAlign(text);
    }

    public void printLeftAlign(String text) throws jpos.JposException {
        posPrinterFacade.printLeftAlign(text);
    }

    public void printItalic(String text) throws jpos.JposException {
        posPrinterFacade.printItalic(text);
    }

    public void printMarginBottom() throws jpos.JposException {
        posPrinterFacade.printMarginBottom();
    }

    public void printLine(String text) throws jpos.JposException {
        posPrinterFacade.printLine(text);
    }

    public void printDoubleHeightDoubleWidth(String text) throws jpos.JposException {
        posPrinterFacade.printDoubleHeightDoubleWidth(text);
    }

    public void printDoubleHeight(String text) throws jpos.JposException {
        posPrinterFacade.printDoubleHeight(text);
    }

    public void printDoubleWidth(String text) throws jpos.JposException {
        posPrinterFacade.printDoubleWidth(text);
    }

    public void printBold(String text) throws jpos.JposException {
        posPrinterFacade.printBold(text);
    }

    public void printUnderlined(String text) throws jpos.JposException {
        posPrinterFacade.printUnderlined(text);
    }

    public void printASCIITable() throws jpos.JposException {
        posPrinterFacade.printASCIITable();
    }

    @Override
    public void setDevice(BaseControl device) {
        posPrinterFacade.setDevice(device);
    }

    @Override
    public String getLogicalName() {
        return posPrinterFacade.getLogicalName();
    }

    @Override
    public void setLogicalName(String logicalName) {
        posPrinterFacade.setLogicalName(logicalName);
    }

    @Override
    public void open(String logicalName) throws JposException, NoLogicalNameException, NoDeviceException {
        posPrinterFacade.open(logicalName);
    }

    @Override
    public void claim(Integer timeout) throws JposException, NoLogicalNameException, NoDeviceException {
        posPrinterFacade.claim(timeout);
    }

    @Override
    public void enable() throws NoLogicalNameException, NoDeviceException, JposException {
    	log.trace("enable()");
        posPrinterFacade.enable();
    }

    @Override
    public void disable() throws JposException, NoLogicalNameException, NoDeviceException {
    	log.trace("disable()");
        posPrinterFacade.disable();
    }

    @Override
    public void release() throws NoLogicalNameException, NoDeviceException, JposException {
        posPrinterFacade.release();
    }

    @Override
    public void close() throws NoLogicalNameException, NoDeviceException, JposException {
        posPrinterFacade.close();
    }

    @Override
    public void prepareDevice(String logicalName, BaseControl device) throws JposException, NoLogicalNameException, NoDeviceException {
        posPrinterFacade.prepareDevice(logicalName, device);
    }

    @Override
    public void finishDevice() throws JposException, NoLogicalNameException, NoDeviceException {
        posPrinterFacade.finishDevice();
        posPrinterFacade = null;
    }

    public void printBuffer(String text, List<byte[]> constants) throws JposException {
    	posPrinterFacade.printBuffer(text, constants);
    }
    
	public void endLineBuffer() throws JposException {
		posPrinterFacade.endLineBuffer();
	}
    
}
