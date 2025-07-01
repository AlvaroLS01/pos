

package com.comerzzia.pos.devices.drivers.javapos;

import com.comerzzia.pos.devices.drivers.javapos.facade.exceptions.NoDeviceException;
import com.comerzzia.pos.devices.drivers.javapos.facade.exceptions.NoLogicalNameException;
import com.comerzzia.pos.devices.drivers.javapos.facade.jpos.CommonDeviceCatFacade;
import com.comerzzia.pos.devices.drivers.javapos.facade.jpos.LineDisplayFacade;

import jpos.BaseControl;
import jpos.JposException;
import jpos.LineDisplay;

public class JavaPosDisplay extends CommonDeviceCatFacade {

    private static JavaPosDisplay visor;

    private LineDisplayFacade lineDisplayFacade;

    private JavaPosDisplay() {
    }

    public static JavaPosDisplay getVisor() {
        createVisor();
        return visor;
    }

    private synchronized static void createVisor() {
        if (visor == null) {
            visor = new JavaPosDisplay();
        }
    }

    public void inicializar(String logicalName) {
    	if (lineDisplayFacade == null) {
    		lineDisplayFacade = new LineDisplayFacade(logicalName);
        }
    }

    @Override
    public LineDisplay getDevice() {
        return lineDisplayFacade.getDevice();
    }

    public void clearText() throws jpos.JposException {
        lineDisplayFacade.clearText();
    }

    public void displayText(String text) throws jpos.JposException {
        lineDisplayFacade.displayText(text);
    }

    public void displayText(String text, int mode) throws jpos.JposException {
        lineDisplayFacade.displayText(text, mode);
    }

    public void displayTextAt(String text, int row, int column, int mode) throws jpos.JposException {
        lineDisplayFacade.displayTextAt(text, row, column, mode);
    }

    public void displayTextAt(String text, int row, int column) throws jpos.JposException {
        lineDisplayFacade.displayTextAt(text, row, column);
    }

    public void displayRightAlignText(String text) throws jpos.JposException {
        lineDisplayFacade.displayRightAlignText(text);
    }

    public void displayLine(String text) throws jpos.JposException {
        lineDisplayFacade.displayLine(text);
    }

    public void displayLineRightAlign(String text) throws jpos.JposException {
        lineDisplayFacade.displayLineRightAlign(text);
    }

    public void scrollLeft(Integer columns) throws jpos.JposException {
        lineDisplayFacade.scrollLeft(columns);
    }

    public void scrollRight(Integer columns) throws jpos.JposException {
        lineDisplayFacade.scrollRight(columns);
    }

    public void scrollDown(Integer rows) throws jpos.JposException {
        lineDisplayFacade.scrollDown(rows);
    }

    public void scrollUp(Integer rows) throws jpos.JposException {
        lineDisplayFacade.scrollUp(rows);
    }

    public int getColumns() throws jpos.JposException {
        return lineDisplayFacade.getColumns();
    }
    
    public int getRows() throws jpos.JposException {
        return lineDisplayFacade.getRows();
    }


    @Override
    public void setDevice(BaseControl device) {
        lineDisplayFacade.setDevice(device);
    }

    @Override
    public String getLogicalName() {
        return lineDisplayFacade.getLogicalName();
    }

    @Override
    public void setLogicalName(String logicalName) {
        lineDisplayFacade.setLogicalName(logicalName);
    }

    @Override
    public void open(String logicalName) throws JposException, NoLogicalNameException, NoDeviceException {
        lineDisplayFacade.open(logicalName);
    }

    @Override
    public void claim(Integer timeout) throws JposException, NoLogicalNameException, NoDeviceException {
        lineDisplayFacade.claim(timeout);
    }

    @Override
    public void enable() throws NoLogicalNameException, NoDeviceException, JposException {
        lineDisplayFacade.enable();
    }

    @Override
    public void disable() throws JposException, NoLogicalNameException, NoDeviceException {
        lineDisplayFacade.disable();
    }

    @Override
    public void release() throws NoLogicalNameException, NoDeviceException, JposException {
        lineDisplayFacade.release();
    }

    @Override
    public void close() throws NoLogicalNameException, NoDeviceException, JposException {
        lineDisplayFacade.close();
    }

    @Override
    public void prepareDevice() throws NoLogicalNameException, NoDeviceException, JposException {
        lineDisplayFacade.prepareDevice();
    }

    @Override
    public void prepareDevice(String logicalName, BaseControl device) throws JposException, NoLogicalNameException, NoDeviceException {
        lineDisplayFacade.prepareDevice(logicalName, device);
    }

    @Override
    public void finishDevice() throws JposException, NoLogicalNameException, NoDeviceException {
        lineDisplayFacade.finishDevice();
    }
}
